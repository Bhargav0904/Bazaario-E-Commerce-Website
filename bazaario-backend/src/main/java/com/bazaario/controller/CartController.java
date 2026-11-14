package com.bazaario.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bazaario.dto.AddToCartRequest;
import com.bazaario.dto.CartResponse;
import com.bazaario.dto.UpdateCartItemRequest;
import com.bazaario.security.UserPrincipal;
import com.bazaario.service.CartService;

import jakarta.validation.Valid;

/*
 * Every method here takes the user from @AuthenticationPrincipal - i.e. from the
 * verified JWT - never from a request parameter. That's what stops someone passing
 * ?userId=5 to read another person's cart, which is a real risk in the servlet
 * version's approach of trusting whatever the client sends.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping
	public CartResponse getCart(@AuthenticationPrincipal UserPrincipal principal) {
		return cartService.getCart(principal.getUser().getUserId());
	}

	@PostMapping("/items")
	public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody AddToCartRequest request) {
		return ResponseEntity.ok(cartService.addToCart(principal.getUser().getUserId(), request));
	}

	@PutMapping("/items/{cartItemId}")
	public ResponseEntity<CartResponse> updateItem(@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable Integer cartItemId, @Valid @RequestBody UpdateCartItemRequest request) {
		return ResponseEntity.ok(
				cartService.updateQuantity(principal.getUser().getUserId(), cartItemId, request.quantity()));
	}

	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<CartResponse> removeItem(@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable Integer cartItemId) {
		return ResponseEntity.ok(cartService.removeItem(principal.getUser().getUserId(), cartItemId));
	}

	@DeleteMapping
	public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
		cartService.clearCart(principal.getUser().getUserId());
		return ResponseEntity.noContent().build();
	}
}
