package com.bazaario.exception;

// A custom checked-vs-unchecked design choice: this extends RuntimeException (unchecked)
// so service methods don't need "throws ResourceNotFoundException" on every signature -
// GlobalExceptionHandler catches it centrally instead.
public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
