package com.bazaario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bazaario.dto.CategoryRequest;
import com.bazaario.dto.CategoryResponse;
import com.bazaario.service.CategoryService;

import jakarta.validation.Valid;

@RestController
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	// Public - the shop's category filter needs this without a login
	@GetMapping("/api/categories")
	public List<CategoryResponse> getAll() {
		return categoryService.getAllCategories();
	}

	@GetMapping("/api/categories/{id}")
	public CategoryResponse getOne(@PathVariable Integer id) {
		return categoryService.getCategoryById(id);
	}

	// Admin-only (SecurityConfig restricts /api/admin/** to ROLE_ADMIN)
	@PostMapping("/api/admin/categories")
	public ResponseEntity<CategoryResponse> add(@Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.addCategory(request));
	}

	@PutMapping("/api/admin/categories/{id}")
	public ResponseEntity<CategoryResponse> update(@PathVariable Integer id,
			@Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.updateCategory(id, request));
	}

	@DeleteMapping("/api/admin/categories/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
