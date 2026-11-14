package com.bazaario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bazaario.dto.ProductRequest;
import com.bazaario.dto.ProductResponse;
import com.bazaario.entity.Category;
import com.bazaario.entity.Product;
import com.bazaario.exception.ResourceNotFoundException;
import com.bazaario.repository.CategoryRepository;
import com.bazaario.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream()
				.map(ProductResponse::fromEntity)
				.toList(); // Stream API + method reference - both Advanced Java staples
	}

	@Override
	public ProductResponse getProductById(Integer id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
		return ProductResponse.fromEntity(product);
	}

	@Override
	public List<ProductResponse> getProductsByCategory(Integer categoryId) {
		return productRepository.findByCategory_CategoryId(categoryId).stream()
				.map(ProductResponse::fromEntity)
				.toList();
	}

	@Override
	public List<ProductResponse> searchProducts(String keyword) {
		return productRepository
				.findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword).stream()
				.map(ProductResponse::fromEntity)
				.toList();
	}

	@Override
	public ProductResponse addProduct(ProductRequest request) {
		Category category = categoryRepository.findById(request.categoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));

		Product product = new Product();
		product.setProductName(request.productName());
		product.setDescription(request.description());
		product.setPrice(request.price());
		product.setQuantity(request.quantity());
		product.setCategory(category);
		product.setImageUrl(request.imageUrl());

		return ProductResponse.fromEntity(productRepository.save(product));
	}

	@Override
	public ProductResponse updateProduct(Integer id, ProductRequest request) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
		Category category = categoryRepository.findById(request.categoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));

		product.setProductName(request.productName());
		product.setDescription(request.description());
		product.setPrice(request.price());
		product.setQuantity(request.quantity());
		product.setCategory(category);
		product.setImageUrl(request.imageUrl());

		return ProductResponse.fromEntity(productRepository.save(product));
	}

	@Override
	public void deleteProduct(Integer id) {
		if (!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Product not found: " + id);
		}
		productRepository.deleteById(id);
	}
}
