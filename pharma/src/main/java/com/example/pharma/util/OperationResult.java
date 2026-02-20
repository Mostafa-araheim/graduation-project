package com.example.pharma.util;

import java.util.Collections;
import java.util.List;

public record OperationResult<T>(
        boolean success,
        List<ErrorDetail> errors,
        T data
) {
    // Success with data
    public static <T> OperationResult<T> Success(T data) {
        return new OperationResult<>(true, Collections.emptyList(), data);
    }

    // Success without data
    public static <T> OperationResult<T> Success() {
        return Success(null);
    }

    // Failure with multiple errors
    public static <T> OperationResult<T> Failure(List<ErrorDetail> errors) {
        return new OperationResult<>(false, errors, null);
    }

    // Failure with a single error
    public static <T> OperationResult<T> Failure(String code, String message) {
        return new OperationResult<>(false, List.of(new ErrorDetail(code, message)), null);
    }
}
