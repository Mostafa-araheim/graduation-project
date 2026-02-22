package com.example.pharma.exception.access;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class AccountDisabledException extends BaseException {
    public AccountDisabledException() {
        super("Account is disabled", ErrorCode.ACCOUNT_DISABLED);
    }
}
