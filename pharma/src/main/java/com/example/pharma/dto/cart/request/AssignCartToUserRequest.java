package com.example.pharma.dto.cart.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignCartToUserRequest(

        @NotNull
        List<CartItemQuantityRequest> items,

        @NotBlank
        String cartName

) {}
