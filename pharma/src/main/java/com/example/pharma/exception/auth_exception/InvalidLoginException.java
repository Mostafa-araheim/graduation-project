package com.example.pharma.exception.auth_exception;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException(String message) {
        super(message);
    }
}
