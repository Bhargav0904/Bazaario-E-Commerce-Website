package com.bazaario.dto;

public record AuthResponse(
	String token,
	Integer userId,
	String fullName,
	String email,
	String role
) {
}
