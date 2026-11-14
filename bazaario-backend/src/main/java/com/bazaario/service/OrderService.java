package com.bazaario.service;

import java.util.List;

import com.bazaario.dto.OrderResponse;
import com.bazaario.dto.PlaceOrderRequest;
import com.bazaario.entity.OrderStatus;

public interface OrderService {
	OrderResponse placeOrderFromCart(Integer userId, PlaceOrderRequest request);
	OrderResponse getOrderById(Integer userId, Integer orderId, boolean isAdmin);
	List<OrderResponse> getOrdersForUser(Integer userId);
	List<OrderResponse> getAllOrders();
	OrderResponse updateOrderStatus(Integer orderId, OrderStatus status);
}
