package com.example.pharma.exception.auth_exception;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED);
    }
}
