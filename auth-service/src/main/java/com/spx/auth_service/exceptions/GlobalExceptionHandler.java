package com.spx.auth_service.exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import com.spx.auth_service.dto.ApiErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
    // 401 - BAD CREDENTIALS
    // ==========================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED,"Unauthorized", "Invalid username or password", "Provide valid credentials", request);
    }

    // ==========================================================
    // 404 - NOT FOUND
    // ==========================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {} - {}", ex.getResourceName(), ex.getIdentifier());

        String message = String.format("%s with identifier '%s' not found", ex.getResourceName(), ex.getIdentifier());

        return buildError(HttpStatus.NOT_FOUND,"Resource not found", message, "Verify the resource identifier", request);
    }

    // ==========================================================
    // 405 - METHOD NOT ALLOWED
    // ==========================================================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildError(HttpStatus.METHOD_NOT_ALLOWED,"Method not allowed", ex.getMessage(), request);
    }

    // ==========================================================
    // 409 - DUPLICATE KEY
    // ==========================================================

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleDuplicateKey(DuplicateKeyException ex, WebRequest request) {
        return buildError( HttpStatus.CONFLICT,"Conflict","Resource already exists", request);
    }

    // ==========================================================
    // 500 - INTERNAL SERVER ERROR
    // ==========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleServerError(Exception ex, WebRequest request) {
        log.error("Internal Server Error", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error","Unexpected server error", request);
    }

    // ==========================================================
    // 503 - SERVICE UNAVAILABLE
    // ==========================================================

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleServiceUnavailable(ServiceUnavailableException ex, WebRequest request) {
        log.error("Service unavailable", ex);
        return buildError(HttpStatus.SERVICE_UNAVAILABLE,"Service Unavailable", "Service is temporarily unavailable", request);
    }
}