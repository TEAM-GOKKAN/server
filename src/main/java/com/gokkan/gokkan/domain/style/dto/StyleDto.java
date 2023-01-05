package com.gokkan.gokkan.domain.style.dto;

import com.gokkan.gokkan.domain.style.domain.Style;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


public class StyleDto {


	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@Builder
	@Schema(name = "스타일 생성 request")
	public static class CreateRequest {

		@NotNull(message = "name 은 null 일 수 없습니다.")
		private String name;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@Builder
	@Schema(name = "스타일 수정 request")
	public static class UpdateRequest {

		@NotNull(message = "id 은 null 일 수 없습니다.")
		private Long id;

		@NotNull(message = "name 은 null 일 수 없습니다.")
		private String name;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@Builder
	@Schema(name = "스타일 response")
	public static class Response {

		private Long id;
		private String name;

		public static Response toResponse(Style style) {
			return Response.builder()
				.id(style.getId())
				.name(style.getName())
				.build();
		}
	}
}
