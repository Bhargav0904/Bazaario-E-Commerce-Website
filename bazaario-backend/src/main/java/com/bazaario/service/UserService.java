package com.bazaario.service;

import java.util.List;

import com.bazaario.dto.ChangePasswordRequest;
import com.bazaario.dto.UpdateProfileRequest;
import com.bazaario.dto.UserResponse;

public interface UserService {
	UserResponse getProfile(Integer userId);
	UserResponse updateProfile(Integer userId, UpdateProfileRequest request);
	void changePassword(Integer userId, ChangePasswordRequest request);
	List<UserResponse> getAllUsers();
	void deleteUser(Integer userId);
}
