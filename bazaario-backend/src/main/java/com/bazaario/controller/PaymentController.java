package com.bazaario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bazaario.dto.PaymentRequest;
import com.bazaario.dto.PaymentResponse;
import com.bazaario.entity.Role;
import com.bazaario.security.UserPrincipal;
import com.bazaario.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	public ResponseEntity<PaymentResponse> pay(@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody PaymentRequest request) {
		boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
		return ResponseEntity.ok(paymentService.processPayment(principal.getUser().getUserId(), request, isAdmin));
	}

	@GetMapping("/order/{orderId}")
	public PaymentResponse getForOrder(@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable Integer orderId) {
		boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
		return paymentService.getPaymentForOrder(principal.getUser().getUserId(), orderId, isAdmin);
	}
}
