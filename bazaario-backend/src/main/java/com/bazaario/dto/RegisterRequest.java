package com.bazaario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// A Java record - Core/Advanced Java feature (since Java 16): a concise, immutable data
// carrier. This one line generates the constructor, getters (fullName() not getFullName()),
// equals(), hashCode(), and toString() automatically - no boilerplate needed.
public record RegisterRequest(

	@NotBlank(message = "Full name is required")
	String fullName,

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email address")
	String email,

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	String password,

	String phone,
	String address
) {
}
