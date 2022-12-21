package com.gokkan.gokkan.domain.item.controller;

import com.gokkan.gokkan.domain.item.dto.ItemDto;
import com.gokkan.gokkan.domain.item.service.ItemService;
import com.gokkan.gokkan.domain.member.domain.Member;
import com.gokkan.gokkan.global.security.oauth.token.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

	private final ItemService itemService;

	@Operation(summary = "상품 생성", description = "상품 생성, Amazon S3에 파일 업로드, 업로드 된 이미지 url 상품에 저장")
	@ApiResponse(responseCode = "201", description = "생성된 상품 반환", content = @Content(schema = @Schema(implementation = ItemDto.Response.class)))
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
	@Transactional
	public ResponseEntity<?> create(
		@Parameter(description = "상품 생성 정보", required = true, content = @Content(schema = @Schema(implementation = ItemDto.CreateRequest.class)))
		@RequestPart ItemDto.CreateRequest request,
		@Parameter(description = "상품 이미지 파일 (여러 파일 업로드 가능)", required = true)
		@RequestPart List<MultipartFile> imageItemFiles,
		@Parameter(description = "검수 이미지 파일 (여러 파일 업로드 가능)", required = true)
		@RequestPart List<MultipartFile> imageCheckFiles,
		@Parameter(hidden = true)
		@CurrentMember Member member) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(itemService.create(request, imageItemFiles, imageCheckFiles, member));
	}

	@Operation(summary = "상품 조회", description = "itemId에 해당하는 상품 조회")
	@ApiResponse(responseCode = "200", description = "조회한 상품 반환", content = @Content(schema = @Schema(implementation = ItemDto.Response.class)))
	@GetMapping("")
	public ResponseEntity<?> read(
		@Parameter(description = "상품 아이디", required = true)
		@RequestParam Long itemId) {
		return ResponseEntity.ok(itemService.read(itemId));
	}

	@Operation(summary = "상품 삭제", description = "itemId에 해당하는 상품 삭제")
	@ApiResponse(responseCode = "200", description = "itemId에 해당하는 상품 삭제")
	@DeleteMapping
	public ResponseEntity<?> delete(
		@Parameter(description = "상품 아이디", required = true)
		@RequestParam Long itemId,
		@Parameter(hidden = true)
		@CurrentMember Member member) {
		return ResponseEntity.ok(itemService.delete(itemId, member));
	}

	@Operation(summary = "상품 수정", description = "상품 수정, Amazon S3에 파일 업로드, 수정된 이미지 url 상품에 저장")
	@ApiResponse(responseCode = "201", description = "수정된 상품 반환", content = @Content(schema = @Schema(implementation = ItemDto.Response.class)))
	@PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
	@Transactional
	public ResponseEntity<?> update(
		@Parameter(description = "상품 수정 정보", required = true, content = @Content(schema = @Schema(implementation = ItemDto.UpdateRequest.class)))
		@RequestPart ItemDto.UpdateRequest request,
		@Parameter(description = "상품 이미지 파일 (여러 파일 업로드 가능)", required = true)
		@RequestPart List<MultipartFile> imageItemFiles,
		@Parameter(description = "검수 이미지 파일 (여러 파일 업로드 가능)", required = true)
		@RequestPart List<MultipartFile> imageCheckFiles,
		@Parameter(hidden = true)
		@CurrentMember Member member) {

		return ResponseEntity.ok(
			itemService.update(request, imageItemFiles, imageCheckFiles, member));
	}

	@Operation(summary = "상품 임시 생성", description = "상품 임시 생성, 상품 아이디만 반환")
	@ApiResponse(responseCode = "201", description = "상품 임시 생성, 상품 아이디만 반환")
	@PostMapping("/temp")
	@Transactional
	public ResponseEntity<?> create(
		@Parameter(hidden = true)
		@CurrentMember Member member) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(itemService.createTemporary(member));
	}
}
