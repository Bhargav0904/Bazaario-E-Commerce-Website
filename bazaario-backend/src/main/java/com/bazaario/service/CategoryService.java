package com.bazaario.service;

import java.util.List;

import com.bazaario.dto.CategoryRequest;
import com.bazaario.dto.CategoryResponse;

public interface CategoryService {
	List<CategoryResponse> getAllCategories();
	CategoryResponse getCategoryById(Integer id);
	CategoryResponse addCategory(CategoryRequest request);
	CategoryResponse updateCategory(Integer id, CategoryRequest request);
	void deleteCategory(Integer id);
}
