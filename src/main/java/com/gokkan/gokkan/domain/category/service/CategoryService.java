package com.gokkan.gokkan.domain.category.service;

import com.gokkan.gokkan.domain.category.domain.Category;
import com.gokkan.gokkan.domain.category.dto.CategoryDto;
import com.gokkan.gokkan.domain.category.exception.CategoryErrorCode;
import com.gokkan.gokkan.domain.category.exception.CategoryException;
import com.gokkan.gokkan.domain.category.repository.CategoryRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryDto.Response create(CategoryDto.CreateRequest request) {
		Category parent;
		Category category = request.toEntity();

		if (request.getParent() == null) {
			parent = categoryRepository.findByName("root")
				.orElseGet(() -> categoryRepository.save(Category.builder()
					.level(0)
					.parent(null)
					.children(new ArrayList<>())
					.name("root")
					.build()));
		} else {
			parent = categoryRepository.findByName(request.getParent()).orElseThrow(
				() -> new CategoryException(CategoryErrorCode.NOT_FOUND_PARENT_CATEGORY));
		}

		category.setLevel(parent.getLevel() + 1);
		Category.addRelation(parent, category);

		return CategoryDto.Response.toResponse(categoryRepository.save(category));
	}

	public CategoryDto.Response read(String name) {
		Category category = categoryRepository.findByName(name)
			.orElseThrow(() -> new CategoryException(CategoryErrorCode.NOT_FOUND_CATEGORY));

		return CategoryDto.Response.toResponse(category);
	}

}
