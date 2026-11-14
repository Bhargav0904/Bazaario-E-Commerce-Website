package com.bazaario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "cart_items")
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_item_id")
	private Integer cartItemId;

	@ManyToOne
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Positive
	@Column(nullable = false)
	private Integer quantity;

	public CartItem() {
	}

	public CartItem(Cart cart, Product product, Integer quantity) {
		this.cart = cart;
		this.product = product;
		this.quantity = quantity;
	}

	public Integer getCartItemId() {
		return cartItemId;
	}

	public void setCartItemId(Integer cartItemId) {
		this.cartItemId = cartItemId;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	// Convenience method - not persisted
	public java.math.BigDecimal getSubtotal() {
		return product.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
	}
}
