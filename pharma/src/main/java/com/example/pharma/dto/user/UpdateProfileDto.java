package com.example.pharma.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(max = 100, message = "Name must be less than 100 characters")
        String name,

        @Size(max = 20, message = "Phone must be less than 20 characters")
        String phone
) {}
