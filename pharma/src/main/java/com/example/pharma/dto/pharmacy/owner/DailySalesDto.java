package com.example.pharma.dto.pharmacy.owner;

import java.math.BigDecimal;

public record DailySalesDto(
        String date, // YYYY-MM-DD
        BigDecimal revenue,
        Long orderCount
) {}
