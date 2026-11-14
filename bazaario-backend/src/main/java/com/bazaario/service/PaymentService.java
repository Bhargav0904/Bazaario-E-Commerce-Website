package com.bazaario.service;

import com.bazaario.dto.PaymentRequest;
import com.bazaario.dto.PaymentResponse;

public interface PaymentService {
	PaymentResponse processPayment(Integer userId, PaymentRequest request, boolean isAdmin);
	PaymentResponse getPaymentForOrder(Integer userId, Integer orderId, boolean isAdmin);
}
