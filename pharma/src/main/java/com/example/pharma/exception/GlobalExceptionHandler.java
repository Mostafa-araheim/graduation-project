package com.example.pharma.exception;

import com.example.pharma.dto.ErrorResponse;
import com.example.pharma.exception.auth_exception.EmailAlreadyRegisteredException;
import com.example.pharma.exception.auth_exception.InvalidLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex)
    {
        return new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(InvalidLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidLogin(InvalidLoginException ex)
    {
        return new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

}
