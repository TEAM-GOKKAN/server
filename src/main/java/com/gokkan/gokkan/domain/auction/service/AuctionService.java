package com.gokkan.gokkan.domain.auction.service;

import com.gokkan.gokkan.domain.auction.domain.Auction;
import com.gokkan.gokkan.domain.auction.domain.History;
import com.gokkan.gokkan.domain.auction.domain.dto.AuctionDto.ResponseAuctionHistory;
import com.gokkan.gokkan.domain.auction.domain.dto.AuctionDto.ResponseAuctionInfo;
import com.gokkan.gokkan.domain.auction.exception.AuctionErrorCode;
import com.gokkan.gokkan.domain.auction.repository.AuctionHistoryRepository;
import com.gokkan.gokkan.domain.auction.repository.AuctionRepository;
import com.gokkan.gokkan.global.exception.exception.RestApiException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionService {

	private final AuctionRepository auctionRepository;
	private final AuctionHistoryRepository auctionHistoryRepository;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional(readOnly = true)
	public ResponseAuctionInfo getAuctionInfo(Long auctionId) {
		log.info("현재가, 마감시간 조회");
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new RestApiException(AuctionErrorCode.AUCTION_NOT_FOUND));
		return ResponseAuctionInfo.builder()
			.auctionEndDateTime(auction.getEndDateTime())
			.currentPrice(auction.getCurrentPrice())
			.build();
	}

	@Transactional(readOnly = true)
	public List<ResponseAuctionHistory> getAuctionHistory(Long auctionId) {
		Auction auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new RestApiException(AuctionErrorCode.AUCTION_NOT_FOUND));
		List<History> auctionHistories = getHistory(auction.getId());
		if (auctionHistories.isEmpty()) {
			return new ArrayList<>();
		}
		LocalDateTime bidDateTime = auction.getStartDateTime();
		List<ResponseAuctionHistory> responseAuctionHistories = new ArrayList<>();
		for (History h : auctionHistories) {
			int secretValue = (int) (
				(h.getMemberId() + auction.getId()) +
					(bidDateTime.getYear() * bidDateTime.getMonth().getValue()) +
					(bidDateTime.getSecond() + bidDateTime.getMinute() + bidDateTime.getHour()));
			String secretId = String.format("%05d",
				secretValue).substring(0, 5);
			responseAuctionHistories.add(
				ResponseAuctionHistory.of(secretId, h.getPrice(), h.getBidTime()));
		}
		return responseAuctionHistories;
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