package com.bazaario.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bazaario.dto.PaymentRequest;
import com.bazaario.dto.PaymentResponse;
import com.bazaario.entity.Order;
import com.bazaario.entity.OrderStatus;
import com.bazaario.entity.Payment;
import com.bazaario.entity.PaymentMethod;
import com.bazaario.entity.PaymentStatus;
import com.bazaario.exception.BadRequestException;
import com.bazaario.exception.ResourceNotFoundException;
import com.bazaario.repository.OrderRepository;
import com.bazaario.repository.PaymentRepository;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;

	public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {
		this.paymentRepository = paymentRepository;
		this.orderRepository = orderRepository;
	}

	/*
	 * PLACEHOLDER for a real gateway. Right now this records the payment as SUCCESS
	 * immediately (except COD, which stays PENDING until delivery).
	 *
	 * To integrate Razorpay later, this is the method to change:
	 *   1. Add a separate endpoint that calls Razorpay's Orders API and returns their
	 *      order_id to the frontend.
	 *   2. The frontend opens Razorpay's checkout widget with that id.
	 *   3. Razorpay returns razorpay_payment_id + razorpay_signature to the frontend,
	 *      which POSTs them here.
	 *   4. VERIFY that signature with your secret key before setting SUCCESS - that
	 *      verification step is what makes the payment trustworthy, and is the piece
	 *      this placeholder is missing.
	 */
	@Override
	public PaymentResponse processPayment(Integer userId, PaymentRequest request, boolean isAdmin) {
		Order order = orderRepository.findById(request.orderId())
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.orderId()));

		if (!isAdmin && !order.getUser().getUserId().equals(userId)) {
			throw new BadRequestException("That order does not belong to you");
		}

		paymentRepository.findByOrder_OrderId(order.getOrderId()).ifPresent(existing -> {
			if (existing.getPaymentStatus() == PaymentStatus.SUCCESS) {
				throw new BadRequestException("This order has already been paid for");
			}
		});

		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setPaymentMethod(request.paymentMethod());
		payment.setAmount(order.getTotalAmount());
		payment.setTransactionId(request.transactionId());

		if (request.paymentMethod() == PaymentMethod.COD) {
			payment.setPaymentStatus(PaymentStatus.PENDING);
		} else {
			payment.setPaymentStatus(PaymentStatus.SUCCESS);
		}

		Payment saved = paymentRepository.save(payment);

		// Either way the order is confirmed - COD just means payment lands on delivery.
		order.setStatus(OrderStatus.CONFIRMED);
		orderRepository.save(order);

		return PaymentResponse.fromEntity(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentResponse getPaymentForOrder(Integer userId, Integer orderId, boolean isAdmin) {
		Payment payment = paymentRepository.findByOrder_OrderId(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("No payment found for order " + orderId));

		if (!isAdmin && !payment.getOrder().getUser().getUserId().equals(userId)) {
			throw new BadRequestException("That payment does not belong to you");
		}

		return PaymentResponse.fromEntity(payment);
	}
}
