package com.example.pharma.dto;

public record OwnerLoginVerifyRequest(
        String loginId,
        String code
) {}