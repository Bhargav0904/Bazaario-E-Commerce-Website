package com.bazaario.dto;

import java.math.BigDecimal;

import com.bazaario.entity.CartItem;

public record CartItemResponse(
	Integer cartItemId,
	Integer productId,
	String productName,
	String imageUrl,
	BigDecimal price,
	Integer quantity,
	Integer availableStock,
	BigDecimal subtotal
) {
	public static CartItemResponse fromEntity(CartItem item) {
		return new CartItemResponse(
				item.getCartItemId(),
				item.getProduct().getProductId(),
				item.getProduct().getProductName(),
				item.getProduct().getImageUrl(),
				item.getProduct().getPrice(),
				item.getQuantity(),
				item.getProduct().getQuantity(),
				item.getSubtotal()
		);
	}
}
