package com.bazaario.dto;

import java.math.BigDecimal;

import com.bazaario.entity.OrderItem;

public record OrderItemResponse(
	Integer orderItemId,
	Integer productId,
	String productName,
	String imageUrl,
	Integer quantity,
	BigDecimal price,
	BigDecimal subtotal
) {
	public static OrderItemResponse fromEntity(OrderItem item) {
		return new OrderItemResponse(
				item.getOrderItemId(),
				item.getProduct().getProductId(),
				item.getProduct().getProductName(),
				item.getProduct().getImageUrl(),
				item.getQuantity(),
				item.getPrice(),
				item.getSubtotal()
		);
	}
}
