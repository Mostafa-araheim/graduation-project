package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCartRequest(

        @NotNull(message = "UserId is required")
        @Positive(message = "UserId must be positive")
        Long userId,

        @NotBlank(message = "Cart name cannot be blank")
        @Size(max = 100, message = "Cart name cannot exceed 100 characters")
        String name
) {}