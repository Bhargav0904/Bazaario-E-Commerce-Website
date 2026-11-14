package com.bazaario.dto;

import java.time.LocalDateTime;

import com.bazaario.entity.User;

// Note there is no password field here - this is exactly why DTOs exist rather than
// returning the User entity directly: the password hash never leaves the server.
public record UserResponse(
	Integer userId,
	String fullName,
	String email,
	String phone,
	String address,
	String role,
	LocalDateTime createdAt
) {
	public static UserResponse fromEntity(User user) {
		return new UserResponse(
				user.getUserId(),
				user.getFullName(),
				user.getEmail(),
				user.getPhone(),
				user.getAddress(),
				user.getRole().name(),
				user.getCreatedAt()
		);
	}
}
