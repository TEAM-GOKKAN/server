package com.gokkan.gokkan.domain.auction.controller;

import com.gokkan.gokkan.domain.auction.domain.dto.AuctionDto.ResponseAuctionHistory;
import com.gokkan.gokkan.domain.auction.domain.dto.AuctionDto.ResponseAuctionInfo;
import com.gokkan.gokkan.domain.auction.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auction")
@Tag(name = "경매 컨트롤러", description = "경매 컨트롤러")
public class AuctionController {

	private final AuctionService auctionService;

	@GetMapping
	@Operation(summary = "경매 정보 현재가, 마감시간", description = "경매 정보 현재가, 마감시간")
	@ApiResponse(description = "경매 정보", content = @Content(schema = @Schema(implementation = ResponseAuctionInfo.class)))
	public ResponseEntity<ResponseAuctionInfo> getAuctionInfo(
		@Parameter(description = "경매 아이디") @RequestParam Long auctionId) {
		return ResponseEntity.ok(auctionService.getAuctionInfo(auctionId));
	}

	@GetMapping("/history")
	@Operation(summary = "경매 히스토리 조회(리스트임!!)", description = "경매 히스토리 조회(리스트임!!)")
	@ApiResponse(description = "경매 히스토리(리스트임!!)", content = @Content(schema = @Schema(implementation = ResponseAuctionHistory.class)))
	public ResponseEntity<List<ResponseAuctionHistory>> getAuctionHistory(
		@Parameter(description = "경매 아이디") @RequestParam Long auctionId) {
		return ResponseEntity.ok(auctionService.getAuctionHistory(auctionId));
	}
}