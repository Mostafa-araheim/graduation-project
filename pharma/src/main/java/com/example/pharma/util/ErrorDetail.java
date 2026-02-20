package com.example.pharma.util;

public record ErrorDetail(String code, String message) {
    public static ErrorDetail of(String code, String message) {
        return new ErrorDetail(code, message);
    }
}
