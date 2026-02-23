package com.example.pharma.dto.common;

import com.example.pharma.dto.exception.ErrorResponse;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        ErrorResponse error,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(String message, T data)
    {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }
    public static <T> ApiResponse<T> failure(String message, ErrorResponse errors)
    {
        return new ApiResponse<>(false, message, null, errors, LocalDateTime.now());
    }
}
