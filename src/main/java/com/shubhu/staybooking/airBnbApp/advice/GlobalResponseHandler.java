package com.shubhu.staybooking.airBnbApp.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.util.List;

/**
 * Global response handler that wraps all successful REST API responses
 * inside the {@link ApiResponse} wrapper to provide a consistent
 * response structure across the application.
 *
 * <p>Certain endpoints such as Swagger/OpenAPI and Actuator are excluded
 * because they require their original response format.</p>
 */
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    /**
     * Determines whether this response advice should be applied.
     *
     * <p>Returning {@code true} ensures that every controller response
     * passes through {@code beforeBodyWrite()} before being sent to
     * the client.</p>
     *
     * @param returnType the controller method return type
     * @param converterType the selected HTTP message converter
     * @return {@code true} to apply this advice to all responses
     */
    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * Intercepts the response body before it is written to the HTTP response.
     *
     * <p>If the response is not already wrapped inside {@link ApiResponse},
     * it is automatically wrapped to maintain a standardized API response
     * format. Swagger/OpenAPI and Actuator endpoints are skipped to avoid
     * interfering with their expected response structure.</p>
     *
     * @param body the response body returned by the controller
     * @param returnType the controller method return type
     * @param selectedContentType the selected response media type
     * @param selectedConverterType the selected HTTP message converter
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @return the original response for excluded endpoints or an
     *         {@link ApiResponse} wrapping the response body
     */
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        List<String> excludedRoutes = List.of("/v3/api-docs", "/actuator");

        boolean isExcluded = excludedRoutes
                .stream()
                .anyMatch(route -> request.getURI().getPath().contains(route));

        if (body instanceof ApiResponse<?> || isExcluded) {
            return body;
        }
        return new ApiResponse<>(body);
    }
}