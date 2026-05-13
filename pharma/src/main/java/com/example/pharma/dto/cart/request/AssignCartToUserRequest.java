package com.example.pharma.dto.cart.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignCartToUserRequest(

        @NotEmpty(message = "Cart items are required")
        List<@Valid CartItemRequest> items
) {}