package com.bazaario.dto;

import com.bazaario.entity.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
	@NotNull Integer orderId,
	@NotNull PaymentMethod paymentMethod,
	// Supplied by a real gateway (Razorpay etc.) after the user pays. Null for COD,
	// or when you're testing without a gateway wired up.
	String transactionId
) {
}
