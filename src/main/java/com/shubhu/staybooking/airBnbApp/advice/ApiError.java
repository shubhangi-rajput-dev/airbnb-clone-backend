package com.shubhu.staybooking.airBnbApp.advice;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.util.List;

/**
 * Represents the standard error response returned by the REST API
 * whenever an exception occurs during request processing.
 *
 * <p>This class provides a consistent error response structure across
 * the application, making it easier for clients to parse and handle
 * error responses.</p>
 * <p>Typical response format:</p>
 * <pre>
 * {
 *   "status": "BAD_REQUEST",
 *   "message": "Invalid user input",
 *   "subErrors": [
 *     "Email should not be empty",
 *     "Password length must be greater than 8"
 *   ]
 * }
 * </pre>
 */
@Data
@Builder
public class ApiError {
    /**
     * HTTP status associated with the error.
     * <p>Common values include:</p>
     * <ul>
     *   <li>{@code BAD_REQUEST (400)} - Invalid request data.</li>
     *   <li>{@code UNAUTHORIZED (401)} - Authentication failed.</li>
     *   <li>{@code FORBIDDEN (403)} - Access denied.</li>
     *   <li>{@code NOT_FOUND (404)} - Requested resource was not found.</li>
     *   <li>{@code INTERNAL_SERVER_ERROR (500)} - Unexpected server error.</li>
     * </ul>
     */
    private HttpStatus status;
    /**
     * Human-readable message describing the error.
     * <p>Example:</p>
     * <pre>
     * Hotel not found with id: 10
     * </pre>
     */
    private String message;
    /**
     * Collection of detailed error messages.
     * <p>Typically used to return validation errors when multiple
     * request fields fail validation.</p>
     * <p>Example:</p>
     * <pre>
     * [
     *   "Name cannot be empty",
     *   "Price must be greater than zero"
     * ]
     * </pre>
     */
    private List<String> subErrors;
}