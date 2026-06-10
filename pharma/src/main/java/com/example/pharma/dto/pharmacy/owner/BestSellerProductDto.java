package com.example.pharma.dto.pharmacy.owner;

import java.math.BigDecimal;

public record BestSellerProductDto(
        Long productId,
        String productName,
        Long quantitySold,
        BigDecimal totalRevenue
) {}
