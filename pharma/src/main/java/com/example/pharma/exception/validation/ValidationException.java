package com.example.pharma.exception.validation;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class ValidationException extends BaseException {
    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }
}
