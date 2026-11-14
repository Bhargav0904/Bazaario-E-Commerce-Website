package com.bazaario.dto;

// Simple wrapper so endpoints that just confirm an action still return JSON
// (e.g. {"message": "Password updated"}) rather than a bare string.
public record MessageResponse(String message) {
}
