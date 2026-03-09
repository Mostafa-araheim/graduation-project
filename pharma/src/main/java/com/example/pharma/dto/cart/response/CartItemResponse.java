package com.example.pharma.dto.cart.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId,
        Long quantity,
        BigDecimal pricePerUnit,
        BigDecimal totalPrice
) {}
