package com.bazaario.service;

import com.bazaario.dto.AddToCartRequest;
import com.bazaario.dto.CartResponse;

public interface CartService {
	CartResponse getCart(Integer userId);
	CartResponse addToCart(Integer userId, AddToCartRequest request);
	CartResponse updateQuantity(Integer userId, Integer cartItemId, Integer quantity);
	CartResponse removeItem(Integer userId, Integer cartItemId);
	void clearCart(Integer userId);
}
