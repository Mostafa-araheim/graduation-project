package com.example.pharma.exception.resource;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.exception.common.BaseException;

public class EntityAlreadyExistsException extends BaseException {
    public EntityAlreadyExistsException(String message) {
        super(message, ErrorCode.ENTITY_ALREADY_EXISTS);
    }
}