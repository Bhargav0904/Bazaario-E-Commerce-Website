package com.bazaario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazaario.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

	Optional<CartItem> findByCart_CartIdAndProduct_ProductId(Integer cartId, Integer productId);
}
