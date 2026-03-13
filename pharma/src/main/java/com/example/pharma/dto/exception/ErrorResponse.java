package com.example.pharma.dto.exception;

import com.example.pharma.exception.common.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        ErrorCode errorCode,
        String message,
        Long status,
        Instant timestamp,
        String path
) {

    public ErrorResponse(BaseException ex, HttpServletRequest request) {
        this(
                ex.getErrorCode(),
                ex.getMessage(),
                Long.valueOf(ex.getStatus().value()),
                Instant.now(),
                request.getRequestURI()
        );
    }
}