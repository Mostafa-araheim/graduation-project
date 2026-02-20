package com.example.pharma.dto;

public record CustomerLoginVerifyRequest(
        String loginId,
        String code
) {}