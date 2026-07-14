package com.shubhu.staybooking.airBnbApp.exception;

/**
 * Exception thrown when attempting to register a user
 * with an email address that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {
    /**
     * Creates a new UserAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message describing the exception.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}