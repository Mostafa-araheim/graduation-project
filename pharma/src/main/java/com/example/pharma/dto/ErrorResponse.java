package com.example.pharma.dto;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ErrorResponse(String message, int status, LocalDateTime timestamp) {
    public ErrorResponse(String message, int status)
    {
        this(message, status, LocalDateTime.now());
    }

}
