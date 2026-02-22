package com.example.pharma.exception.access;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException(String message) {
        super(message, ErrorCode.ACCESS_DENIED);
    }
}
