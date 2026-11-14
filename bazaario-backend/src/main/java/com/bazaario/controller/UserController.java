package com.bazaario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bazaario.dto.ChangePasswordRequest;
import com.bazaario.dto.MessageResponse;
import com.bazaario.dto.UpdateProfileRequest;
import com.bazaario.dto.UserResponse;
import com.bazaario.security.UserPrincipal;
import com.bazaario.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// --- Logged-in user's own profile ---

	@GetMapping("/api/profile")
	public UserResponse getProfile(@AuthenticationPrincipal UserPrincipal principal) {
		return userService.getProfile(principal.getUser().getUserId());
	}

	@PutMapping("/api/profile")
	public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody UpdateProfileRequest request) {
		return ResponseEntity.ok(userService.updateProfile(principal.getUser().getUserId(), request));
	}

	@PutMapping("/api/profile/password")
	public ResponseEntity<MessageResponse> changePassword(@AuthenticationPrincipal UserPrincipal principal,
			@Valid @RequestBody ChangePasswordRequest request) {
		userService.changePassword(principal.getUser().getUserId(), request);
		return ResponseEntity.ok(new MessageResponse("Password updated successfully"));
	}

	// --- Admin user management ---

	@GetMapping("/api/admin/users")
	public List<UserResponse> allUsers() {
		return userService.getAllUsers();
	}

	@DeleteMapping("/api/admin/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
