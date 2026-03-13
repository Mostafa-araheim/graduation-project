package com.example.pharma.security;

public record AuthenticatedUser(
        Long userId,
        String email
) {}
