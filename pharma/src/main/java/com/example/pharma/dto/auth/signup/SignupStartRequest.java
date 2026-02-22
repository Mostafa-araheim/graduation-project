package com.example.pharma.dto.auth.signup;

import com.example.pharma.model.entity.core.UserRole;

public record SignupStartRequest(
        String email,
        String name,
        UserRole role
) {}