package com.shubhu.staybooking.airBnbApp.advice;

import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.exception.UnAuthorisedException;
import com.shubhu.staybooking.airBnbApp.exception.UserAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.stream.Collectors;

/*
 * @RestControllerAdvice is a combination of:
 *
 * 1. @ControllerAdvice
 *    -> Allows this class to handle exceptions globally
 *       from all controllers.
 * 2. @ResponseBody
 *    -> Converts returned objects into JSON responses automatically.
 *
 * This class works as a centralized exception handling mechanism
 * for the complete REST API.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException thrown when a requested resource
     * does not exist in the database
     * @param exception the thrown ResourceNotFoundException
     * @return a standardized 404 (NOT_FOUND) error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(
            ResourceNotFoundException exception) {
        log.error("Resource not found: {}", exception.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    /**
     * Handles JWT-related exceptions such as invalid, expired,
     * or tampered JWT tokens.
     * @param exception the thrown JwtException
     * @return a standardized 401 (UNAUTHORIZED) error response
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(
            JwtException exception) {
        log.error("JWT exception occurred: {}", exception.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    /**
     * Handles authorization failures when an authenticated user
     * attempts to access a resource without sufficient permissions.
     * @param exception the thrown AccessDeniedException
     * @return a standardized 403 (FORBIDDEN) error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(
            AccessDeniedException exception) {
        log.warn("Access denied: {}", exception.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    /**
     * Handles all unexpected exceptions that are not processed
     * by any specific exception handler.
     * @param exception the unexpected exception
     * @return a standardized 500 (INTERNAL_SERVER_ERROR) error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(
            Exception exception) {
        log.error("Internal server error occurred: {}", exception.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    /**
     * Handles validation failures triggered by the {@code @Valid} annotation.
     * Collects all validation messages and returns them in the response.
     * @param exception the validation exception thrown by Spring
     * @return a standardized 400 (BAD_REQUEST) error response containing
     *         all validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationError(
            MethodArgumentNotValidException exception) {
        log.warn("Input validation failed: {}", exception.getMessage());
        List<String> errors = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Input validation failed")
                .subErrors(errors)
                .build();
        return buildErrorResponseEntity(apiError);
    }

    /**
     * Handles authentication failures when a user provides invalid
     * or missing authentication credentials.
     * Examples:
     * - Invalid username or password
     * - Missing authentication credentials
     * - Disabled or locked user account
     *
     * @param exception the thrown AuthenticationException
     * @return a standardized 401 (UNAUTHORIZED) error response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(
            AuthenticationException exception) {
        log.error("Authentication failed: {}", exception.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Authentication failed. Please check your credentials.")
                .build();
        return buildErrorResponseEntity(apiError);
    }

    /**
     * Handles UserAlreadyExistsException thrown when a user attempts to
     * register with an email address that already exists.
     *
     * @param exception the thrown UserAlreadyExistsException.
     * @return ResponseEntity containing an error response with HTTP 409 (Conflict).
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyExistsException(
            UserAlreadyExistsException exception) {
        log.warn("User already exists: {}", exception.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    /**
     * Handles UnAuthorisedException thrown when a user attempts to perform
     * an operation without the required authorization.
     * <p>Examples include:
     * <ul>
     *     <li>Accessing another user's resource.</li>
     *     <li>Performing an operation without sufficient permissions.</li>
     *     <li>Failing a custom authorization check implemented in the application.</li>
     * </ul>
     * </p>
     * @param exception the thrown UnAuthorisedException.
     * @return a standardized 403 (FORBIDDEN) error response.
     */
    @ExceptionHandler(UnAuthorisedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnAuthorisedException(
            UnAuthorisedException exception) {
        log.warn("User is not authorised: {}", exception.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    /**
     * Handles JSON parsing errors that occur when the request payload
     * cannot be converted into the expected Java object format.
     * Common cases include invalid JSON structure or invalid enum values.
     *
     * @param exception the thrown HttpMessageNotReadableException
     * @return a standardized 400 (BAD_REQUEST) error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonParseError(
            HttpMessageNotReadableException exception) {
        log.warn("Invalid request payload: {}", exception.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Invalid request payload: " + exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }
    /**
     * Builds a standardized API error response wrapped inside a ResponseEntity.
     * @param apiError the error details to include in the response
     * @return ResponseEntity containing the standardized API response
     */
    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }
}
