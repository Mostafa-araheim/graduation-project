package com.example.pharma.dto.auth.signup;

import com.example.pharma.model.entity.core.UserRole;

public record SignupSession(
        String email,
        String name,
        UserRole role,
        String codeHash,
        Long attempts
) {}
