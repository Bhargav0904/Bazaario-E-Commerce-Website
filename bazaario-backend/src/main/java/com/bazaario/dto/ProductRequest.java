package com.bazaario.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest(
	@NotBlank String productName,
	String description,
	@NotNull @Positive BigDecimal price,
	@NotNull @PositiveOrZero Integer quantity,
	@NotNull Integer categoryId,
	String imageUrl
) {
}
