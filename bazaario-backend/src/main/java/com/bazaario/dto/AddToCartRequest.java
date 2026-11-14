package com.bazaario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddToCartRequest(
	@NotNull Integer productId,
	@NotNull @Positive Integer quantity
) {
}
