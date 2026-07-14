package com.shubhu.staybooking.airBnbApp.advice;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Generic API response wrapper used to standardize all API responses.
 *
 * <p>This class supports both successful and error responses by
 * including either the response data or error details along with
 * the timestamp of when the response was generated.</p>
 *
 * @param <T> the type of the response data
 */
@Data
public class ApiResponse<T> {
    /**
     * Timestamp indicating when the API response was created.
     */
    private LocalDateTime timeStamp;

    /**
     * Indicates whether the API response is successful.
     */
    private boolean success;

    /**
     * Contains the response payload for successful requests.
     */
    private T data;

    /**
     * Contains error details when the request fails.
     * This field remains null for successful responses.
     */
    private ApiError error;

    /**
     * Default constructor.
     * Initializes the response timestamp with the current date and time.
     */
    public ApiResponse() {
        this.timeStamp = LocalDateTime.now();
    }

    /**
     * Creates a successful API response.
     * @param data the response payload to be returned to the client
     */
    public ApiResponse(T data) {
        this();
        this.success = true;
        this.data = data;
    }

    /**
     * Creates an error API response.
     * @param error the error details describing the failure
     */
    public ApiResponse(ApiError error) {
        this();
        this.success = false;
        this.error = error;
    }
}