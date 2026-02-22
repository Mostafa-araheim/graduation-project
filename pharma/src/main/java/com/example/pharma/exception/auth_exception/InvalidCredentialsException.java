package com.example.pharma.exception.auth_exception;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException() {
        super("Invalid username or password", ErrorCode.INVALID_CREDENTIALS);
    }
}
