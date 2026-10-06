package com.example.demo.exceptions;

/** Thrown when the admin API key is missing or invalid. */
public class UnauthorizedAdminAccessException extends RuntimeException {
    public UnauthorizedAdminAccessException(String message) {
        super(message);
    }
}
