package com.example.pharma.exception;

import com.example.pharma.dto.ApiResponse;
import com.example.pharma.exception.auth_exception.EmailAlreadyRegisteredException;
import com.example.pharma.exception.auth_exception.InvalidLoginException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyRegisteredException.class)

    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex)
    {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidLogin(InvalidLoginException ex)
    {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity< ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("Validation failed", errors));
    }

}
