package com.gokkan.gokkan.domain.auction.service;

import com.gokkan.gokkan.domain.auction.domain.Auction;
import com.gokkan.gokkan.domain.auction.domain.AuctionHistory;
import com.gokkan.gokkan.domain.auction.domain.History;
import com.gokkan.gokkan.domain.auction.domain.type.AuctionStatus;
import com.gokkan.gokkan.domain.auction.exception.AuctionErrorCode;
import com.gokkan.gokkan.domain.auction.repository.AuctionHistoryRepository;
import com.gokkan.gokkan.domain.auction.repository.AuctionRepository;
import com.gokkan.gokkan.domain.member.domain.Member;
import com.gokkan.gokkan.domain.member.exception.MemberErrorCode;
import com.gokkan.gokkan.global.exception.exception.RestApiException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {

	private final SimpMessageSendingOperations simpMessageSendingOperations;
	private final AuctionRepository auctionRepository;
	private final RedissonClient redissonClient;
	private final RedisTemplate<String, String> redisTemplate;
	private final AuctionHistoryRepository auctionHistoryRepository;


	@Transactional
	public void bidding(Member member, Long auctionId, Long bidPrice) {
		if (member == null) {
			throw new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND);
		}
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new RestApiException(
				AuctionErrorCode.AUCTION_NOT_FOUND));
		if (auction.getAuctionStatus() == AuctionStatus.ENDED) {
			throw new RestApiException(AuctionErrorCode.AUCTION_ALREADY_ENDED);
		}

		final String lockName = auctionId + ":lock";
		final RLock lock = redissonClient.getLock(lockName);
		try {
			if (!lock.tryLock(1, 3, TimeUnit.SECONDS)) {
				throw new RestApiException(AuctionErrorCode.AUCTION_ANOTHER_USER_IS_BIDDING);
			}
		} catch (InterruptedException e) {
			throw new RestApiException(AuctionErrorCode.AUCTION_FAILED_TO_GET_LOCK);
		}
		log.info("lock acquired");

		List<History> history = getHistory(auctionId);
		Long currentPrice;
		if (history.isEmpty()) {
			log.info("history is empty");
			currentPrice = auction.getCurrentPrice();
		} else {
			History lastHistory = history.get(0);
			if (lastHistory.getMemberId().equals(member.getId())) {
//				throw new RestApiException(AuctionErrorCode.AUCTION_ALREADY_BID);
			}
			currentPrice = lastHistory.getPrice();
		}

		if (currentPrice >= bidPrice) {
			throw new RestApiException(
				AuctionErrorCode.AUCTION_PRICE_IS_LOWER_THAN_CURRENT_PRICE);
		}

		log.info("현재 진행중인 사람 : {} & 입찰가 : {}원", member.getId(), bidPrice);
		History currentHistory = History.builder()
			.memberId(member.getId())
			.price(bidPrice)
			.bidTime(LocalDateTime.now())
			.build();
		history.add(0, currentHistory);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		LocalDateTime bidDateTime = auction.getStartDateTime();
		for (History h : history) {
			JSONObject object = new JSONObject();
			int secretValue = (int) (
				(h.getMemberId() + auction.getId()) +
					(bidDateTime.getYear() * bidDateTime.getMonth().getValue()) +
					(bidDateTime.getSecond() + bidDateTime.getMinute() + bidDateTime.getHour()));
			String secretId = String.format("%05d",
				secretValue).substring(0, 5);
			object.put("memberId", secretId);
			object.put("price", h.getPrice());
			object.put("bidTime", h.getBidTime().toString());
			jsonArray.add(object);
		}
		jsonObject.put("history", jsonArray);
		jsonObject.put("currentPrice", bidPrice);

		auction.setCurrentPrice(bidPrice);
		auction.setMember(member);
		auctionRepository.save(auction);
		auctionHistoryRepository.save(
			AuctionHistory.builder()
				.member(member)
				.auction(auction)
				.price(bidPrice)
				.bidDateTime(LocalDateTime.now())
				.build());
		saveHistory(auction.getId(), currentHistory);
		simpMessageSendingOperations.convertAndSend("/topic/" + auctionId,
			jsonObject.toString());

		if (lock.isLocked()) {
			lock.unlock();
			log.info("lock released");
		}
		log.info("입찰 성공");
	}

	private void saveHistory(Long auctionId, History currentHistory) {
		redisTemplate.opsForList().leftPush(auctionId.toString(), currentHistory.toString());
	}


	private List<History> getHistory(Long auctionId) {
		List<String> StringHistory = redisTemplate.opsForList()
			.range(String.valueOf(auctionId), 0, -1);
		if (StringHistory == null || StringHistory.isEmpty()) {
			return new ArrayList<>();
		}
		return StringHistory.stream()
			.map(History::toHistory)
			.collect(Collectors.toList());
	}

}
