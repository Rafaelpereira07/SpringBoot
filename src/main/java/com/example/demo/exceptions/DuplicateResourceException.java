package com.example.demo.exceptions;

/** Thrown when a uniqueness rule would be violated (e.g. duplicate e-mail or slug). */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
