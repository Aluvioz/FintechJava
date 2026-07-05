package com.fintech.api.dto;

public record AuthResponse(
        String token,
        String tokenType
) {
    public AuthResponse(String token) {
        this(token, "Bearer");
    }
}