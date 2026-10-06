package com.example.demo.exceptions;

/**
 * Thrown when an authenticated student tries to watch a lesson video without
 * an active subscription. Kept distinct from a generic 403 so the client can
 * show a specific "subscribe to continue" call to action.
 */
public class SubscriptionRequiredException extends RuntimeException {
    public SubscriptionRequiredException(String message) {
        super(message);
    }
}
