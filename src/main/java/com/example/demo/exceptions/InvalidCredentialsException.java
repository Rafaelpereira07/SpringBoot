package com.example.demo.exceptions;

/** Thrown when login credentials are invalid. */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
