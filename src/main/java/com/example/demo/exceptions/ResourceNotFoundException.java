package com.example.demo.exceptions;

/** Thrown when a requested entity does not exist (or is soft-deleted). */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
