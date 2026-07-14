package com.shubhu.staybooking.airBnbApp.exception;

/**
 * Exception thrown when a user is not authorized to perform an operation.
 */
public class UnAuthorisedException extends RuntimeException{
    /**
     * Creates a new unauthorized exception with the specified message.
     *
     * @param message error message describing the authorization failure
     */
    public UnAuthorisedException(String message) {
        super(message);
    }
}
