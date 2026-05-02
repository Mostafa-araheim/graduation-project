package com.example.pharma.dto.auth;

public record AuthVerification(
        Long userId,
        String email
) {
}