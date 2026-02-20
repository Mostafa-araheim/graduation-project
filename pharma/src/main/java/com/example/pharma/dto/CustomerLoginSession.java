package com.example.pharma.dto;

public record CustomerLoginSession(
        String email,
        String codeHash,
        int attempts
) {}