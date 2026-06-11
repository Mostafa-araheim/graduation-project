package com.example.pharma.exception;

import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.dto.exception.ErrorCode;
import com.example.pharma.dto.exception.ErrorResponse;
import com.example.pharma.exception.common.BaseException;
import com.example.pharma.exception.prescription.PrescriptionScanFailedException;
import com.example.pharma.exception.prescription.PrescriptionScanTimeoutException;
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
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBaseException(
            BaseException ex,
            HttpServletRequest request
    ) {
        log.error("Business Exception occurred: {}", ex.getMessage());

        ErrorResponse error = buildError(ex, request);


        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.failure("", error));
    }

    /**
     * Handles @Valid on @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValid(
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
                .body(ApiResponse.failure("",error));
    }

    /**
     * Handles validation on request params / path variables
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleConstraintViolation(
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
                .body(ApiResponse.failure("",error));
    }

    /**
     * Handles malformed JSON or wrong data types
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleHttpMessageNotReadable(
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
                .body(ApiResponse.failure("",error));
    }


    /*
        Prescription Exceptions
     */
    @ExceptionHandler(PrescriptionScanFailedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handlePrescriptionScanFailed(PrescriptionScanFailedException ex, HttpServletRequest request)
    {
        ErrorResponse errorResponse = buildError(ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("", errorResponse));
    }

    @ExceptionHandler(PrescriptionScanTimeoutException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handlePrescriptionScanTimeoutFailed(PrescriptionScanTimeoutException ex, HttpServletRequest request)
    {
        ErrorResponse errorResponse = buildError(ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("", errorResponse));
    }

    /**
     * Fallback for any unhandled exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGenericException(
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
                .body(ApiResponse.failure("",error));
    }

    private ErrorResponse buildError(
            String message,
            ErrorCode errorCode,
            HttpServletRequest request
    ) {


        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .status((long)errorCode.getStatus().value())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }

    private ErrorResponse buildError(BaseException exception, HttpServletRequest request) {
        return new ErrorResponse(exception, request);
    }

}