package com.example.demo.exceptions;

/** Generic 409-style violation of a business rule (e.g. re-activating an already active subscription). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
