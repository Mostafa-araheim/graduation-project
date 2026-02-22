package com.example.pharma.exception.system;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class DatabaseException extends BaseException {
    public DatabaseException(String message) {
        super(message, ErrorCode.DATABASE_ERROR);
    }
}