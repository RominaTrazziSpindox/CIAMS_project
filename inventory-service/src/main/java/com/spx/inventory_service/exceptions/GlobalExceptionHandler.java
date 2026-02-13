package com.spx.inventory_service.exceptions;

import com.spx.inventory_service.dto.ApiErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String DEFAULT_ACTION = "Check request data and try again";

    // ==========================================================
    // BUILDER METHOD (With custom action)
    // ==========================================================

    /*
     * Creates a new ApiErrorResponseDTO object with all its parameters.
     * Put it as a body of a ResponseEntity Object.
     * Set the correct HTTP status code (e.g. 400, 409...)
     */
    private ResponseEntity<ApiErrorResponseDTO> buildError(
            HttpStatus status, String errorTitle, String message, String action, WebRequest request) {

        ApiErrorResponseDTO error = new ApiErrorResponseDTO(
                status.value(),
                errorTitle,
                message,
                action,
                // Uri path
                request.getDescription(false), // NO Client IP exposure
                LocalDateTime.now()
        );

        // The object ResponseEntity is now built by two blocks: 1. status 2. body(error) 3. headers are omitted.
        return ResponseEntity.status(status).body(error);
    }


    // ==========================================================
    // BUILDER METHOD OVERLOAD (With default action)
    // ==========================================================
    private ResponseEntity<ApiErrorResponseDTO> buildError(
            HttpStatus status, String errorTitle, String message, WebRequest request) {

        return buildError(status, errorTitle, message, DEFAULT_ACTION, request);
    }


    // ==========================================================
    // 400 - BAD REQUEST (Client error types)
    // ==========================================================

    /*
     * If the exception comes from Validation (@Valid), extract a user-friendly validation message
     * from the BindingResult.
     * Otherwise, use the message provided by the exception itself.
     */
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class}) // Error types intercepted
    public ResponseEntity<ApiErrorResponseDTO> handleBadRequest (Exception ex, WebRequest request) {

        String message;

        // If the type of error is MethodArgumentNotValidException (@ Valid)...
        if (ex instanceof MethodArgumentNotValidException validationEx) {

            // Step 1: Collect all the validation errors
            var errors = validationEx.getBindingResult().getAllErrors();

            // Step 2: If the list is Empty use a fallback message, else take the first error and its message
            message = errors.isEmpty() ? "Validation error" : errors.get(0).getDefaultMessage();

        } else {

            // If it is not a validation error (@Valid) use the default message (if is != null) or use a fallback message
            message = ex.getMessage() != null ? ex.getMessage() : "Invalid request";
        }

        return buildError(HttpStatus.BAD_REQUEST,"Bad Request", message, request);

    }

    // ==========================================================
    // 404 - NOT FOUND
    // ==========================================================

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNotFound(EntityNotFoundException ex, WebRequest request) {
        return buildError(HttpStatus.NOT_FOUND,"Resource not found", ex.getMessage(),request);
    }


    // ==========================================================
    // 405 - METHOD NOT ALLOWED
    // ==========================================================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildError(HttpStatus.METHOD_NOT_ALLOWED,"Method not allowed", ex.getMessage(),request);
    }


    // ==========================================================
    // 409 - CONFLICT
    // ==========================================================

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleConflict(IllegalStateException ex, WebRequest request) {
        return buildError(HttpStatus.CONFLICT,"Conflict", ex.getMessage(),request);
    }

    // ==========================================================
    // 409 - CONFLICT (Data Integrity)
    // ==========================================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleDataIntegrityConflict(DataIntegrityViolationException ex, WebRequest request) {
        String message = "Operation not allowed: related entities still exist";
        return buildError(HttpStatus.CONFLICT,"Conflict - Data integrity violation", message, request);
    }


    // ==========================================================
    // 500 - INTERNAL SERVER ERROR (FALLBACK)
    // ==========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGeneric(Exception ex, WebRequest request) {
        log.error("Internal server error", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected server error", request);
    }


    // ==========================================================
    // 503 - SERVICE UNAVAILABLE
    // ==========================================================

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleServiceUnavailable(ServiceUnavailableException ex, WebRequest request) {
        log.error("Service unavailable", ex);
        return buildError(HttpStatus.SERVICE_UNAVAILABLE,"Service Unavailable", ex.getMessage(), request);
    }

}
