package com.bazaario.dto;

import com.bazaario.entity.Category;

public record CategoryResponse(
	Integer categoryId,
	String categoryName,
	String description
) {
	public static CategoryResponse fromEntity(Category category) {
		return new CategoryResponse(
				category.getCategoryId(),
				category.getCategoryName(),
				category.getDescription()
		);
	}
}
