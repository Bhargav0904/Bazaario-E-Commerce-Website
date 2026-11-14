package com.bazaario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bazaario.entity.Payment;

public record PaymentResponse(
	Integer paymentId,
	Integer orderId,
	String paymentMethod,
	String paymentStatus,
	LocalDateTime paymentDate,
	BigDecimal amount,
	String transactionId
) {
	public static PaymentResponse fromEntity(Payment payment) {
		return new PaymentResponse(
				payment.getPaymentId(),
				payment.getOrder().getOrderId(),
				payment.getPaymentMethod().name(),
				payment.getPaymentStatus().name(),
				payment.getPaymentDate(),
				payment.getAmount(),
				payment.getTransactionId()
		);
	}
}
