package com.example.pharma.dto.auth;

public record AuthVerificationResult(
        AuthVerification user,
        String jwt
) {
}
