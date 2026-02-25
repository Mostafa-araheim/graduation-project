package com.example.pharma.exception.access;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class IllegalStateException extends BaseException {
    public IllegalStateException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }
}