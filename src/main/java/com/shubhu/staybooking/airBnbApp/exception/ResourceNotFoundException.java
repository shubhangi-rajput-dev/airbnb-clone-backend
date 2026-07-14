package com.shubhu.staybooking.airBnbApp.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Creates a new resource not found exception with the specified message.
     *
     * @param message error message describing the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
