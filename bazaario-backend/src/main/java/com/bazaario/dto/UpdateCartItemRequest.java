package com.bazaario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCartItemRequest(
	// 0 is allowed here and means "remove this item" - see CartServiceImpl.updateQuantity
	@NotNull @PositiveOrZero Integer quantity
) {
}
