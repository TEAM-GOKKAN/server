package com.gokkan.gokkan.domain.auction.domain.dto;

import com.gokkan.gokkan.domain.auction.domain.type.AuctionStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class AuctionDto {

	@Getter
	@Builder
	@Schema(name = "경매 정보", description = "경매 정보")
	public static class ResponseAuctionInfo {

		private LocalDateTime auctionEndDateTime;
		private Long currentPrice;
	}

	@Getter
	@Builder
	@Schema(name = "경매 히스토리", description = "경매 히스토리")
	public static class ResponseAuctionHistory {

		private String memberId;
		private Long price;
		private LocalDateTime bidTime;

		public static ResponseAuctionHistory of(String memberId, Long price,
			LocalDateTime bidTime) {
			return ResponseAuctionHistory.builder()
				.memberId(memberId)
				.price(price)
				.bidTime(bidTime)
				.build();
		}
	}


	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ToString
	@Schema(name = "경매 List filter api request")
	public static class FilterListRequest {

		private String category;
		private List<String> styles;
		@Schema(name = "정렬방식", description = "(마감 시간 역순), (마감 시간 정순) 둘 중 하나로 보내주시면 됩니다. 다른 메세지는 오류납니다.")
		private String sort;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ToString
	@Schema(name = "카테고리 유사 경매 5개 List api request")
	public static class SimilarListRequest {

		private String category;
		private Long auctionId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	@Builder
	@Schema(name = "경매 List filter api 주요 정보 response")
	public static class ListResponse {

		private Long id;
		private Long itemId;

		private String name;

		private String thumbnail;

		private Long currentPrice;

		private String writer;

		private LocalDateTime auctionEndDateTime;

		@QueryProjection
		public ListResponse(
			Long id,
			Long itemId,
			String name,
			String thumbnail,
			Long currentPrice,
			String writer,
			LocalDateTime auctionEndDateTime
		) {
			this.id = id;
			this.itemId = itemId;
			this.name = name;
			this.currentPrice = currentPrice;
			this.thumbnail = thumbnail;
			this.writer = writer;
			this.auctionEndDateTime = auctionEndDateTime;
		}

		@Builder
		public ListResponse(
			Long id,
			Long itemId,
			String name,
			String thumbnail,
			Long currentPrice,
			String writer
		) {
			this.id = id;
			this.itemId = itemId;
			this.name = name;
			this.currentPrice = currentPrice;
			this.thumbnail = thumbnail;
			this.writer = writer;
		}
	}

	@Getter
	@Builder
	@Schema(name = "낙찰된 경매 상세 정보 response")
	public static class SuccessfulBidListResponse {

		private Long id;
		private Long itemId;
		private String name;
		private String thumbnail;
		private Long currentPrice;
		private String writer;
		private LocalDateTime auctionEndDateTime;
		private AuctionStatus auctionStatus;

	}

	@Getter
	@Builder
	@Schema(name = "주문 상세 (배송지)")
	public static class AuctionOrderDetailAddress{
		private String name;
		private String phoneNumber;
		private String address;
		private String addressDetail;
	}

	@Getter
	@Builder
	@Schema(name = "주문 상세 (주문 상품)")
	public static class AuctionOrderDetailItem{
		private Long id;
		private Long itemId;
		private String itemName;
		private String thumbnail;
		private Long price;
	}

	@Getter
	@Builder
	@Schema(name = "주문 상세 (결제 금액)")
	public static class AuctionOrderDetailPaymentAmount{
		private Long hammerPrice;
		private Long fee;
		private Long paymentAmount;
	}
}
