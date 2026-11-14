package com.bazaario.service;

import com.bazaario.dto.AuthResponse;
import com.bazaario.dto.LoginRequest;
import com.bazaario.dto.RegisterRequest;

// Coding to an interface (not the concrete class) is what lets AuthController depend on
// "some AuthService" without caring how it's implemented - classic dependency inversion,
// and it's what makes swapping implementations or writing mock-based unit tests possible.
public interface AuthService {
	AuthResponse register(RegisterRequest request);
	AuthResponse login(LoginRequest request);
}
