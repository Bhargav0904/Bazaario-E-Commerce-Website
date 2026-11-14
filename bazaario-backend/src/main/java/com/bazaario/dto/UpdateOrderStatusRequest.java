package com.bazaario.dto;

import com.bazaario.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
	// Binding straight to the enum means an invalid value like "SHIPPPED" is rejected
	// by Spring's JSON deserializer before it ever reaches the service.
	@NotNull OrderStatus status
) {
}
