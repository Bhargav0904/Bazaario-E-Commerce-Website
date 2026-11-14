package com.bazaario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bazaario.dto.ProductRequest;
import com.bazaario.dto.ProductResponse;
import com.bazaario.service.ProductService;

import jakarta.validation.Valid;

@RestController
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	// --- Public endpoints (see SecurityConfig: GET /api/products/** is permitAll) ---

	@GetMapping("/api/products")
	public List<ProductResponse> getAllProducts(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer categoryId) {

		if (keyword != null && !keyword.isBlank()) {
			return productService.searchProducts(keyword);
		}
		if (categoryId != null) {
			return productService.getProductsByCategory(categoryId);
		}
		return productService.getAllProducts();
	}

	@GetMapping("/api/products/{id}")
	public ProductResponse getProduct(@PathVariable Integer id) {
		return productService.getProductById(id);
	}

	// --- Admin-only endpoints (see SecurityConfig: /api/admin/** requires ROLE_ADMIN) ---

	@PostMapping("/api/admin/products")
	public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
		return ResponseEntity.ok(productService.addProduct(request));
	}

	@PutMapping("/api/admin/products/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable Integer id,
			@Valid @RequestBody ProductRequest request) {
		return ResponseEntity.ok(productService.updateProduct(id, request));
	}

	@DeleteMapping("/api/admin/products/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
}
