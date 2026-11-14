package com.bazaario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.bazaario.entity.Order;

public record OrderResponse(
	Integer orderId,
	Integer userId,
	String customerName,
	LocalDateTime orderDate,
	BigDecimal totalAmount,
	String status,
	String shippingAddress,
	List<OrderItemResponse> items
) {
	public static OrderResponse fromEntity(Order order) {
		return new OrderResponse(
				order.getOrderId(),
				order.getUser().getUserId(),
				order.getUser().getFullName(),
				order.getOrderDate(),
				order.getTotalAmount(),
				order.getStatus().name(),
				order.getShippingAddress(),
				order.getItems().stream().map(OrderItemResponse::fromEntity).toList()
		);
	}
}
