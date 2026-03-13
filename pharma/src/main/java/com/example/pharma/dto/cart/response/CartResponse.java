package com.example.pharma.dto.cart.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        Long totalItems,
        BigDecimal totalPrice,
        Instant updatedAt
        ,String cartName
) {}