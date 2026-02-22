package com.example.pharma.dto.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    /*
     * ===============================
     * 400 - BAD REQUEST (Validation)
     * ===============================
     */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST),
    MALFORMED_JSON(HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    /*
     * ===============================
     * 401 - UNAUTHORIZED
     * ===============================
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED),
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED),

    /*
     * ===============================
     * 403 - FORBIDDEN
     * ===============================
     */
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN),

    /*
     * ===============================
     * 404 - NOT FOUND
     * ===============================
     */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),

    /*
     * ===============================
     * 409 - CONFLICT
     * ===============================
     */
    ENTITY_ALREADY_EXISTS(HttpStatus.CONFLICT),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT),

    /*
     * ===============================
     * 422 - UNPROCESSABLE ENTITY
     * ===============================
     */
    BUSINESS_RULE_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY),
    OPERATION_NOT_ALLOWED(HttpStatus.UNPROCESSABLE_ENTITY),

    /*
     * ===============================
     * 500 - INTERNAL SERVER ERROR
     * ===============================
     */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
