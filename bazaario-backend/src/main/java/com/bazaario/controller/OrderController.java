package com.bazaario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bazaario.dto.OrderResponse;
import com.bazaario.dto.PlaceOrderRequest;
import com.bazaario.dto.UpdateOrderStatusRequest;
import com.bazaario.entity.Role;
import com.bazaario.security.UserPrincipal;
import com.bazaario.service.OrderService;

import jakarta.validation.Valid;

@RestController
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	// --- Customer endpoints ---

	@PostMapping("/api/orders")
	public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody PlaceOrderRequest request) {
		OrderResponse order = orderService.placeOrderFromCart(principal.getUser().getUserId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}

	@GetMapping("/api/orders")
	public List<OrderResponse> myOrders(@AuthenticationPrincipal UserPrincipal principal) {
		return orderService.getOrdersForUser(principal.getUser().getUserId());
	}

	@GetMapping("/api/orders/{id}")
	public OrderResponse getOrder(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Integer id) {
		boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
		return orderService.getOrderById(principal.getUser().getUserId(), id, isAdmin);
	}

	// --- Admin endpoints ---

	@GetMapping("/api/admin/orders")
	public List<OrderResponse> allOrders() {
		return orderService.getAllOrders();
	}

	@PutMapping("/api/admin/orders/{id}/status")
	public ResponseEntity<OrderResponse> updateStatus(@PathVariable Integer id,
			@Valid @RequestBody UpdateOrderStatusRequest request) {
		return ResponseEntity.ok(orderService.updateOrderStatus(id, request.status()));
	}
}
