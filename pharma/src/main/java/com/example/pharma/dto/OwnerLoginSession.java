package com.example.pharma.dto;

public record OwnerLoginSession(
        String email,
        String codeHash,
        int attempts
) {}