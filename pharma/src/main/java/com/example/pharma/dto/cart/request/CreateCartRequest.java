package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCartRequest(


        @NotBlank(message = "Cart name cannot be blank")
        @Size(max = 100, message = "Cart name cannot exceed 100 characters")
        String name
) {}