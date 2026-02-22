package com.example.pharma.dto.auth.login;

import com.example.pharma.model.entity.core.UserRole;

public record LoginStartRequest(
        String email,
        UserRole role
) {}
