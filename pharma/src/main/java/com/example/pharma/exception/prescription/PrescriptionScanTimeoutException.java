package com.example.pharma.exception.prescription;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class PrescriptionScanTimeoutException extends BaseException {
    public PrescriptionScanTimeoutException(String message) {
        super(message, ErrorCode.EXTERNAL_SERVICE_ERROR);
    }
}
