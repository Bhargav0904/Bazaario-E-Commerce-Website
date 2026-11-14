package com.bazaario.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice: one place that catches exceptions thrown by ANY controller,
// so individual controller methods stay focused on the happy path instead of repeating
// try/catch blocks everywhere - replaces the JDBC version's pattern of every DAO method
// catching SQLException and printing a stack trace.
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password"));
	}

	// Triggered automatically when a @Valid-annotated request body (e.g. RegisterRequest)
	// fails its validation annotations (@NotBlank, @Email, etc.) - returns field-level
	// messages instead of a generic 500.
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
				errors.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong: " + ex.getMessage()));
	}
}
