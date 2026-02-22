package com.example.pharma.exception.auth_exception;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException() {
        super("Token has expired", ErrorCode.TOKEN_EXPIRED);
    }
}
