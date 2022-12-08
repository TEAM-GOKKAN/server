package com.gokkan.gokkan.domain.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gokkan.gokkan.domain.category.domain.Category;
import com.gokkan.gokkan.domain.category.dto.CategoryDto;
import com.gokkan.gokkan.domain.category.dto.CategoryDto.CreateRequest;
import com.gokkan.gokkan.domain.category.dto.CategoryDto.Response;
import com.gokkan.gokkan.domain.category.exception.CategoryErrorCode;
import com.gokkan.gokkan.domain.category.exception.CategoryException;
import com.gokkan.gokkan.domain.category.repository.CategoryRepository;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CategoryServiceTest {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepository;


	@DisplayName("01_00. create root category success")
	@Test
	public void test_01_00() {
		//given
		CategoryDto.CreateRequest request =
			CreateRequest.builder()
				.parent(null)
				.name("의자")
				.build();

		System.out.println("===============================");

		//when
		Response response = categoryService.create(request);

		//then
		Category savedCategory = categoryRepository.findById(response.getId()).get();

		assertEquals(response.getName(), request.getName());
		assertEquals(savedCategory.getParent().getName(), "root");
		assertEquals(savedCategory.getParent().getChildren().size(), 1);
		assertEquals(savedCategory.getLevel(), savedCategory.getParent().getLevel() + 1);
	}

	@DisplayName("01_01. create root category success already exist root")
	@Test
	public void test_01_01() {
		//given
		Category root = categoryRepository.save(Category.builder()
			.level(0)
			.name("root")
			.children(new ArrayList<>())
			.build());

		CategoryDto.CreateRequest request =
			CreateRequest.builder()
				.parent(null)
				.name("의자")
				.build();

		System.out.println("===============================");

		//when
		Response response = categoryService.create(request);

		//then
		Category savedCategory = categoryRepository.findById(response.getId()).get();

		assertEquals(response.getName(), request.getName());
		assertEquals(savedCategory.getParent().getName(), "root");
		assertEquals(savedCategory.getParent().getChildren().size(), 1);
		assertEquals(savedCategory.getLevel(), root.getLevel() + 1);
	}

	@DisplayName("01_02. create not root category success")
	@Test
	public void test_01_02() {
		//given
		Category parent = categoryRepository.save(Category.builder()
			.level(1)
			.name("의자")
			.children(new ArrayList<>())
			.build());

		CategoryDto.CreateRequest request =
			CreateRequest.builder()
				.parent("의자")
				.name("좌식 의자")
				.build();

		System.out.println("===============================");

		//when
		Response response = categoryService.create(request);

		//then
		Category savedCategory = categoryRepository.findById(response.getId()).get();

		assertEquals(response.getName(), request.getName());
		assertEquals(savedCategory.getParent().getName(), "의자");
		assertEquals(savedCategory.getParent().getChildren().size(), 1);
		assertEquals(savedCategory.getLevel(), parent.getLevel() + 1);
	}

	@DisplayName("01_03. create not root category fail not found parent")
	@Test
	public void test_01_03() {
		//given
		CategoryDto.CreateRequest request =
			CreateRequest.builder()
				.parent("의자")
				.name("좌식 의자")
				.build();

		System.out.println("===============================");

		//when
		CategoryException categoryException = assertThrows(CategoryException.class,
			() -> categoryService.create(request));

		//then
		assertEquals(categoryException.getErrorCode(), CategoryErrorCode.NOT_FOUND_PARENT_CATEGORY);
	}

	@DisplayName("02_00. read success")
	@Test
	public void test_02_00() {
		//given
		testInput();

		System.out.println("=======================");
		//when
		Response response1 = categoryService.read("의자");
		Response response2 = categoryService.read("책상");
		Response response3 = categoryService.read("root");

		//then
		assertEquals(response1.getChildren().size(), 3);
		assertEquals(response1.getName(), "의자");
		assertEquals(response2.getChildren().size(), 4);
		assertEquals(response2.getName(), "책상");
		assertEquals(response3.getChildren().size(), 2);
		assertEquals(response3.getName(), "root");
	}

	@DisplayName("02_01. read fail not found category")
	@Test
	public void test_02_01() {
		//given

		System.out.println("=======================");
		//when
		CategoryException categoryException = assertThrows(CategoryException.class,
			() -> categoryService.read("의자"));

		//then
		assertEquals(categoryException.getErrorCode(), CategoryErrorCode.NOT_FOUND_CATEGORY);
	}

	private void testInput() {
		categoryService.create(CreateRequest.builder()
			.name("의자")
			.parent(null)
			.build());

		categoryService.create(CreateRequest.builder()
			.parent("의자")
			.name("의자1")
			.build());

		categoryService.create(CreateRequest.builder()
			.parent("의자")
			.name("의자2")
			.build());

		categoryService.create(CreateRequest.builder()
			.parent("의자")
			.name("의자3")
			.build());

		categoryService.create(CreateRequest.builder()
			.name("책상")
			.parent(null)
			.build());

		categoryService.create(CreateRequest.builder()
			.parent("책상")
			.name("책상1")
			.build());

		categoryService.create(CreateRequest.builder()
			.parent("책상")
			.name("책상2")
			.build());

		categoryService.create(CreateRequest.builder()
			.parent("책상")
			.name("책상3")
			.build());

		categoryService.create(CreateRequest.builder()
			.parent("책상")
			.name("책상4")
			.build());
	}
}