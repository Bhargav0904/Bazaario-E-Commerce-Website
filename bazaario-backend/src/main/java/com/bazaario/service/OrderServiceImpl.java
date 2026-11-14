package com.bazaario.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bazaario.dto.OrderResponse;
import com.bazaario.dto.PlaceOrderRequest;
import com.bazaario.entity.Cart;
import com.bazaario.entity.CartItem;
import com.bazaario.entity.Order;
import com.bazaario.entity.OrderItem;
import com.bazaario.entity.OrderStatus;
import com.bazaario.entity.Product;
import com.bazaario.entity.User;
import com.bazaario.exception.BadRequestException;
import com.bazaario.exception.ResourceNotFoundException;
import com.bazaario.repository.CartRepository;
import com.bazaario.repository.OrderRepository;
import com.bazaario.repository.ProductRepository;
import com.bazaario.repository.UserRepository;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final CartService cartService;

	public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository,
			ProductRepository productRepository, UserRepository userRepository, CartService cartService) {
		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.cartService = cartService;
	}

	/*
	 * The whole method runs inside one transaction (@Transactional on the class).
	 * If ANY step throws - insufficient stock on the third item, say - Spring rolls
	 * back everything: the order row, its items, and every stock deduction already
	 * applied. This replaces the manual con.setAutoCommit(false)/commit()/rollback()
	 * juggling that OrderDAO.placeOrder() needed in the JDBC version.
	 */
	@Override
	public OrderResponse placeOrderFromCart(Integer userId, PlaceOrderRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

		Cart cart = cartRepository.findByUser_UserId(userId)
				.orElseThrow(() -> new BadRequestException("Your cart is empty"));

		if (cart.getItems().isEmpty()) {
			throw new BadRequestException("Your cart is empty");
		}

		Order order = new Order();
		order.setUser(user);
		order.setShippingAddress(request.shippingAddress());
		order.setStatus(OrderStatus.PENDING);

		BigDecimal total = BigDecimal.ZERO;

		for (CartItem cartItem : cart.getItems()) {
			Product product = cartItem.getProduct();

			if (product.getQuantity() < cartItem.getQuantity()) {
				throw new BadRequestException(
						"Not enough stock for " + product.getProductName()
						+ " - only " + product.getQuantity() + " left");
			}

			// Deduct stock
			product.setQuantity(product.getQuantity() - cartItem.getQuantity());
			productRepository.save(product);

			// Snapshot the price at purchase time, so a later price change doesn't
			// retroactively alter this order's history.
			OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(), product.getPrice());
			order.getItems().add(orderItem);

			total = total.add(orderItem.getSubtotal());
		}

		order.setTotalAmount(total);
		Order saved = orderRepository.save(order); // cascades to order_items

		cartService.clearCart(userId);

		return OrderResponse.fromEntity(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderResponse getOrderById(Integer userId, Integer orderId, boolean isAdmin) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

		// A customer may only read their own orders; an admin may read any.
		if (!isAdmin && !order.getUser().getUserId().equals(userId)) {
			throw new BadRequestException("That order does not belong to you");
		}

		return OrderResponse.fromEntity(order);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getOrdersForUser(Integer userId) {
		return orderRepository.findByUser_UserIdOrderByOrderDateDesc(userId).stream()
				.map(OrderResponse::fromEntity)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getAllOrders() {
		return orderRepository.findAll().stream()
				.map(OrderResponse::fromEntity)
				.toList();
	}

	@Override
	public OrderResponse updateOrderStatus(Integer orderId, OrderStatus status) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

		// Restock if an order is being cancelled, so inventory stays accurate.
		if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
			for (OrderItem item : order.getItems()) {
				Product product = item.getProduct();
				product.setQuantity(product.getQuantity() + item.getQuantity());
				productRepository.save(product);
			}
		}

		order.setStatus(status);
		return OrderResponse.fromEntity(orderRepository.save(order));
	}
}
