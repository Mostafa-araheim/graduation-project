package com.example.pharma.exception.common;

import com.example.pharma.dto.exception.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@Data
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;

    }
    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }
}