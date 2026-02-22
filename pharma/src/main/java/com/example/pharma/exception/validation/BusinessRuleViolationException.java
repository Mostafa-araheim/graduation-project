package com.example.pharma.exception.validation;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class BusinessRuleViolationException extends BaseException {
    public BusinessRuleViolationException(String message) {
        super(message, ErrorCode.BUSINESS_RULE_VIOLATION);
    }
}