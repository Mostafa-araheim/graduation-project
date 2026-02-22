package com.example.pharma.dto.auth.login;

import com.example.pharma.model.entity.core.UserRole;

public record LoginSession(
        String email,
        UserRole role,
        String codeHash,
        int attempts
) {}
