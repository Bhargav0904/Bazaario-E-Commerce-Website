package com.bazaario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bazaario.dto.CategoryRequest;
import com.bazaario.dto.CategoryResponse;
import com.bazaario.entity.Category;
import com.bazaario.exception.BadRequestException;
import com.bazaario.exception.ResourceNotFoundException;
import com.bazaario.repository.CategoryRepository;
import com.bazaario.repository.ProductRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
	}

	@Override
	public List<CategoryResponse> getAllCategories() {
		return categoryRepository.findAll().stream()
				.map(CategoryResponse::fromEntity)
				.toList();
	}

	@Override
	public CategoryResponse getCategoryById(Integer id) {
		return CategoryResponse.fromEntity(findOrThrow(id));
	}

	@Override
	public CategoryResponse addCategory(CategoryRequest request) {
		Category category = new Category(request.categoryName(), request.description());
		return CategoryResponse.fromEntity(categoryRepository.save(category));
	}

	@Override
	public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
		Category category = findOrThrow(id);
		category.setCategoryName(request.categoryName());
		category.setDescription(request.description());
		return CategoryResponse.fromEntity(categoryRepository.save(category));
	}

	@Override
	public void deleteCategory(Integer id) {
		findOrThrow(id);
		// Refuse rather than let the DB throw a raw foreign-key violation - products
		// would be orphaned, and the user gets a message they can actually act on.
		if (!productRepository.findByCategory_CategoryId(id).isEmpty()) {
			throw new BadRequestException("Cannot delete a category that still has products in it");
		}
		categoryRepository.deleteById(id);
	}

	private Category findOrThrow(Integer id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
	}
}
