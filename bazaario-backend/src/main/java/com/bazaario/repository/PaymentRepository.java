package com.bazaario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazaario.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	Optional<Payment> findByOrder_OrderId(Integer orderId);

	Optional<Payment> findByTransactionId(String transactionId);
}
