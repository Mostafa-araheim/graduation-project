package com.example.pharma.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        List<String> errors,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(String message, T data)
    {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }
    public static <T> ApiResponse<T> failure(String message, List<String> errors)
    {
        return new ApiResponse<>(false, message, null, errors, LocalDateTime.now());
    }
}
