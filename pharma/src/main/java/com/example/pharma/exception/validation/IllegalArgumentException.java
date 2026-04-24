package com.example.pharma.exception.validation;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class IllegalArgumentException extends BaseException {
    public IllegalArgumentException(String message) {
        super(message, ErrorCode.BUSINESS_RULE_VIOLATION);
    }
}
