package com.example.pharma.exception.system;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class ExternalServiceException extends BaseException {
    public ExternalServiceException(String message) {
        super(message, ErrorCode.EXTERNAL_SERVICE_ERROR);
    }
}