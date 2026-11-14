package com.bazaario.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer productId;

	@NotBlank
	@Column(name = "product_name", nullable = false, length = 150)
	private String productName;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Positive
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@PositiveOrZero
	@Column(nullable = false)
	private Integer quantity = 0;

	// FetchType.LAZY: don't load the whole Category row every time you load a Product -
	// only fetched when .getCategory() is actually called. Good default for @ManyToOne.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "image_url", length = 255)
	private String imageUrl;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	public Product() {
	}

	public Product(String productName, String description, BigDecimal price, Integer quantity,
			Category category, String imageUrl) {
		this.productName = productName;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.category = category;
		this.imageUrl = imageUrl;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
