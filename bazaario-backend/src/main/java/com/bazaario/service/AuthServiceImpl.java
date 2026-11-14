package com.bazaario.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bazaario.dto.AuthResponse;
import com.bazaario.dto.LoginRequest;
import com.bazaario.dto.RegisterRequest;
import com.bazaario.entity.Role;
import com.bazaario.entity.User;
import com.bazaario.exception.BadRequestException;
import com.bazaario.repository.UserRepository;
import com.bazaario.security.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	// Constructor injection (preferred over @Autowired on fields) - dependencies are
	// explicit, final, and this class can't be constructed in a half-initialized state.
	public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
			JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new BadRequestException("An account with this email already exists");
		}

		User user = new User();
		user.setFullName(request.fullName());
		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setPhone(request.phone());
		user.setAddress(request.address());
		user.setRole(Role.CUSTOMER);

		User saved = userRepository.save(user);
		String token = jwtUtil.generateToken(saved.getEmail());

		return new AuthResponse(token, saved.getUserId(), saved.getFullName(), saved.getEmail(),
				saved.getRole().name());
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		// Delegates the actual credential check to Spring Security's AuthenticationManager,
		// which uses the DaoAuthenticationProvider bean (CustomUserDetailsService +
		// PasswordEncoder) configured in SecurityConfig. Throws BadCredentialsException
		// automatically on a wrong password - caught by GlobalExceptionHandler.
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new BadRequestException("Invalid email or password"));

		String token = jwtUtil.generateToken(user.getEmail());

		return new AuthResponse(token, user.getUserId(), user.getFullName(), user.getEmail(),
				user.getRole().name());
	}
}
