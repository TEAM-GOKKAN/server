package com.gokkan.gokkan.domain.image.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gokkan.gokkan.domain.image.domain.ImageCheck;
import com.gokkan.gokkan.domain.image.dto.ImageDto.CreateRequest;
import com.gokkan.gokkan.domain.image.exception.ImageErrorCode;
import com.gokkan.gokkan.domain.image.exception.ImageException;
import com.gokkan.gokkan.domain.image.repository.ImageCheckRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImageCheckServiceTest {

	@Mock
	private ImageCheckRepository imageCheckRepository;

	@Mock
	private AwsS3Service awsS3Service;

//	@Mock
//	private ItemRepository itemRepository;

	@InjectMocks
	private ImageCheckService imageCheckService;

	static List<String> urls = List.of("u1", "u2", "u3");

	static ArgumentCaptor<ImageCheck> imageCheckCaptor = ArgumentCaptor.forClass(ImageCheck.class);

	@DisplayName("01_00. save success")
	@Test
	public void test_01_00() {
		//given
		//TODO Item 생성 후
//		given(itemRepository.findById(any())).willReturn(Optional.of(getItem()));
		for (String url : urls) {
			ImageCheck imageCheck = getImageCheck(url);
			lenient().when(imageCheckRepository.save(imageCheck)).thenReturn(imageCheck);
		}

		//when
		imageCheckService.save(getCreateRequest(urls));
		verify(imageCheckRepository, times(3)).save(imageCheckCaptor.capture());

		//then
		List<ImageCheck> imageChecks = imageCheckCaptor.getAllValues();
		for (int i = 0; i < urls.size(); i++) {
			assertEquals(imageChecks.get(i).getUrl(), urls.get(i));
		}
	}

	//TODO Item 생성 후
//	@DisplayName("01_01. save fail not found item")
//	@Test
//	public void test_01_01() {
//		//given
////		given(itemRepository.findById(any())).willReturn(Optional.empty());
//
//		//when
//		ImageException imageException = assertThrows(ImageException.class,
//			() -> imageCheckService.save(getCreateRequest(urls)));
//
//		//then
//		assertEquals(imageException.getErrorCode(), ImageErrorCode.NOT_FOUND_IMAGE_ITEM);
//	}

	@DisplayName("01_02. save fail empty url")
	@Test
	public void test_01_02() {
		//given
//		given(itemRepository.findById(any())).willReturn(Optional.empty());

		//when
		ImageException imageException = assertThrows(ImageException.class,
			() -> imageCheckService.save(getCreateRequest(new ArrayList<>())));

		//then
		assertEquals(imageException.getErrorCode(), ImageErrorCode.EMPTY_URL);
	}

	@DisplayName("01_03. save fail invalid url")
	@Test
	public void test_01_03() {
		//given
		//TODO Item 생성 후
//		given(itemRepository.findById(any())).willReturn(Optional.empty());

		//when
		ImageException imageException = assertThrows(ImageException.class,
			() -> imageCheckService.save(getCreateRequest(new ArrayList<>(List.of("", "")))));

		//then
		assertEquals(imageException.getErrorCode(), ImageErrorCode.INVALID_FORMAT_URL);
	}

	@DisplayName("02_00. delete success")
	@Test
	public void test_02_00() {
		//given
		given(imageCheckRepository.findById(1L))
			.willReturn(Optional.of(getImageCheck("test")));

		//when
		boolean deleted = imageCheckService.delete(1L);
		verify(imageCheckRepository, times(1)).delete(any());
		verify(awsS3Service, times(1)).delete(any());

		//then
		assertTrue(deleted);
	}

	@DisplayName("02_01. delete fail not found")
	@Test
	public void test_02_01() {
		//given
		given(imageCheckRepository.findById(1L))
			.willReturn(Optional.empty());

		//when
		ImageException imageException = assertThrows(ImageException.class,
			() -> imageCheckService.delete(1L));
		verify(imageCheckRepository, times(0)).delete(any());
		verify(awsS3Service, times(0)).delete(any());

		//then
		assertEquals(imageException.getErrorCode(), ImageErrorCode.NOT_FOUND_IMAGE_CHECK);
	}


	private static CreateRequest getCreateRequest(List<String> urls) {
		return CreateRequest.builder()
			.urls(urls)
			.itemId(1L)
			.build();
	}

	private static ImageCheck getImageCheck(String url) {
		return ImageCheck.builder()
			.url(url)
			.build();
	}

}