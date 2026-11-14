package com.bazaario.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Integer paymentId;

	@OneToOne
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 30)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 20)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate = LocalDateTime.now();

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(name = "transaction_id", length = 100)
	private String transactionId;

	public Payment() {
	}

	public Payment(Order order, PaymentMethod paymentMethod, BigDecimal amount, String transactionId) {
		this.order = order;
		this.paymentMethod = paymentMethod;
		this.amount = amount;
		this.transactionId = transactionId;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
