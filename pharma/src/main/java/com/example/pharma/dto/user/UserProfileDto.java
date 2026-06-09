package com.example.pharma.dto.user;

import com.example.pharma.model.entity.core.UserRole;
import java.util.Set;

public record UserProfileDto(
        Long userId,
        String email,
        String name,
        String phone,
        String imageUrl,
        Set<UserRole> roles
) {}
