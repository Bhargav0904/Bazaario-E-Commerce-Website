package com.bazaario.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@NotBlank
	@Column(name = "full_name", nullable = false, length = 100)
	private String fullName;

	@NotBlank
	@Email
	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@NotBlank
	@Column(nullable = false)
	private String password; // stored as a BCrypt hash - see security/JwtUtil + PasswordEncoder bean

	@Column(length = 20)
	private String phone;

	@Column(length = 255)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role = Role.CUSTOMER;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	public User() {
	}

	public User(String fullName, String email, String password, String phone, String address, Role role) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.address = address;
		this.role = role;
	}

	// Getters and setters

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
