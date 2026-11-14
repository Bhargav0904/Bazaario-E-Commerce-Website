package com.bazaario.dto;

import java.math.BigDecimal;

import com.bazaario.entity.Product;

public record ProductResponse(
	Integer productId,
	String productName,
	String description,
	BigDecimal price,
	Integer quantity,
	Integer categoryId,
	String categoryName,
	String imageUrl
) {
	// Static factory method mapping an entity to its DTO - keeps this conversion logic
	// in one place instead of repeating it in every controller method that returns a product.
	public static ProductResponse fromEntity(Product product) {
		return new ProductResponse(
				product.getProductId(),
				product.getProductName(),
				product.getDescription(),
				product.getPrice(),
				product.getQuantity(),
				product.getCategory() != null ? product.getCategory().getCategoryId() : null,
				product.getCategory() != null ? product.getCategory().getCategoryName() : null,
				product.getImageUrl()
		);
	}
}
