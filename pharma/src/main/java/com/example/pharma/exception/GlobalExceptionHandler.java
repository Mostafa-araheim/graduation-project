package com.example.pharma.exception;

import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.dto.exception.ErrorResponse;
import com.example.pharma.exception.common.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all custom exceptions extending BaseException
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request
    ) {
        log.error("Business Exception occurred: {}", ex.getMessage());

        ErrorResponse error = buildError(ex, request);


        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }

    /**
     * Handles @Valid on @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", message);

        ErrorResponse error = buildError(
                message
                ,ErrorCode.VALIDATION_ERROR
                ,request
        );


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Handles validation on request params / path variables
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));

        log.warn("Constraint violation: {}", message);

        ErrorResponse error = buildError(
                message
                ,ErrorCode.VALIDATION_ERROR
                ,request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Handles malformed JSON or wrong data types
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        ErrorResponse error = buildError(
                "Malformed JSON request",
                ErrorCode.MALFORMED_JSON
                ,request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Fallback for any unhandled exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = buildError(
                "Internal Server Error",
                ErrorCode.INTERNAL_ERROR,
                request
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    private ErrorResponse buildError(
            String message,
            ErrorCode errorCode,
            HttpServletRequest request
    ) {


        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .status(errorCode.getStatus().value())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }

    private ErrorResponse buildError(BaseException exception, HttpServletRequest request) {
        return new ErrorResponse(exception, request);
    }

}