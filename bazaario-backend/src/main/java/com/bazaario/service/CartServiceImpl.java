package com.bazaario.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bazaario.dto.AddToCartRequest;
import com.bazaario.dto.CartItemResponse;
import com.bazaario.dto.CartResponse;
import com.bazaario.entity.Cart;
import com.bazaario.entity.CartItem;
import com.bazaario.entity.Product;
import com.bazaario.entity.User;
import com.bazaario.exception.BadRequestException;
import com.bazaario.exception.ResourceNotFoundException;
import com.bazaario.repository.CartItemRepository;
import com.bazaario.repository.CartRepository;
import com.bazaario.repository.ProductRepository;
import com.bazaario.repository.UserRepository;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
			ProductRepository productRepository, UserRepository userRepository) {
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public CartResponse getCart(Integer userId) {
		return cartRepository.findByUser_UserId(userId)
				.map(this::toResponse)
				.orElseGet(() -> new CartResponse(null, List.of(), BigDecimal.ZERO, 0));
	}

	@Override
	public CartResponse addToCart(Integer userId, AddToCartRequest request) {
		Cart cart = getOrCreateCart(userId);
		Product product = productRepository.findById(request.productId())
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.productId()));

		Optional<CartItem> existing =
				cartItemRepository.findByCart_CartIdAndProduct_ProductId(cart.getCartId(), product.getProductId());

		int newQuantity = existing.map(CartItem::getQuantity).orElse(0) + request.quantity();

		if (product.getQuantity() < newQuantity) {
			throw new BadRequestException("Only " + product.getQuantity() + " unit(s) available in stock");
		}

		if (existing.isPresent()) {
			CartItem item = existing.get();
			item.setQuantity(newQuantity);
			cartItemRepository.save(item);
		} else {
			cartItemRepository.save(new CartItem(cart, product, request.quantity()));
		}

		return getCart(userId);
	}

	@Override
	public CartResponse updateQuantity(Integer userId, Integer cartItemId, Integer quantity) {
		CartItem item = findItemOwnedBy(userId, cartItemId);

		// Treat 0 as "remove it" so the frontend's quantity stepper can go to zero
		// without needing a separate call.
		if (quantity == 0) {
			cartItemRepository.delete(item);
			return getCart(userId);
		}

		if (item.getProduct().getQuantity() < quantity) {
			throw new BadRequestException("Only " + item.getProduct().getQuantity() + " unit(s) available in stock");
		}

		item.setQuantity(quantity);
		cartItemRepository.save(item);
		return getCart(userId);
	}

	@Override
	public CartResponse removeItem(Integer userId, Integer cartItemId) {
		cartItemRepository.delete(findItemOwnedBy(userId, cartItemId));
		return getCart(userId);
	}

	@Override
	public void clearCart(Integer userId) {
		cartRepository.findByUser_UserId(userId).ifPresent(cart -> {
			cartItemRepository.deleteAll(cart.getItems());
			cart.getItems().clear();
		});
	}

	private Cart getOrCreateCart(Integer userId) {
		return cartRepository.findByUser_UserId(userId).orElseGet(() -> {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
			return cartRepository.save(new Cart(user));
		});
	}

	// Ownership check: stops user A from editing user B's cart item by guessing an id.
	// The JWT tells us who the caller is; the cart item must belong to them.
	private CartItem findItemOwnedBy(Integer userId, Integer cartItemId) {
		CartItem item = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));

		if (!item.getCart().getUser().getUserId().equals(userId)) {
			throw new BadRequestException("That cart item does not belong to you");
		}
		return item;
	}

	private CartResponse toResponse(Cart cart) {
		List<CartItemResponse> items = cart.getItems().stream()
				.map(CartItemResponse::fromEntity)
				.toList();

		BigDecimal total = items.stream()
				.map(CartItemResponse::subtotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add); // Stream reduce - Advanced Java

		return new CartResponse(cart.getCartId(), items, total, items.size());
	}
}
