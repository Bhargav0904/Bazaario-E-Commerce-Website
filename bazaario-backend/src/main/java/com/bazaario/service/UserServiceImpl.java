package com.bazaario.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bazaario.dto.ChangePasswordRequest;
import com.bazaario.dto.UpdateProfileRequest;
import com.bazaario.dto.UserResponse;
import com.bazaario.entity.User;
import com.bazaario.exception.BadRequestException;
import com.bazaario.exception.ResourceNotFoundException;
import com.bazaario.repository.OrderRepository;
import com.bazaario.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, OrderRepository orderRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getProfile(Integer userId) {
		return UserResponse.fromEntity(findOrThrow(userId));
	}

	@Override
	public UserResponse updateProfile(Integer userId, UpdateProfileRequest request) {
		User user = findOrThrow(userId);
		user.setFullName(request.fullName());
		user.setPhone(request.phone());
		user.setAddress(request.address());
		return UserResponse.fromEntity(userRepository.save(user));
	}

	@Override
	public void changePassword(Integer userId, ChangePasswordRequest request) {
		User user = findOrThrow(userId);

		// matches() hashes the supplied plaintext and compares against the stored hash -
		// you never decrypt a BCrypt hash, you only ever re-hash and compare.
		if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
			throw new BadRequestException("Current password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream()
				.map(UserResponse::fromEntity)
				.toList();
	}

	@Override
	public void deleteUser(Integer userId) {
		User user = findOrThrow(userId);

		// Refuse rather than cascade away someone's purchase history - an e-commerce
		// system generally wants order records preserved for accounting.
		if (!orderRepository.findByUser_UserIdOrderByOrderDateDesc(userId).isEmpty()) {
			throw new BadRequestException("Cannot delete a user who has placed orders");
		}

		userRepository.delete(user);
	}

	private User findOrThrow(Integer userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
	}
}
