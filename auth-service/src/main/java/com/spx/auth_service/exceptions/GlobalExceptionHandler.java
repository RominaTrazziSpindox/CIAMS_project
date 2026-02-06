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


    // ==========================================================
    // BUILDER METHOD
    // ==========================================================

    private ResponseEntity<ApiErrorResponseDTO> buildError(
            HttpStatus status, String errorTitle, String message, WebRequest request) {

        // Initializing a new ApiErrorResponseDTO object. It contains the body part of a ResponseEntity object.
        ApiErrorResponseDTO error = new ApiErrorResponseDTO(
                status.value(),
                errorTitle,
                message,
                "Check request data and try again", // Implementation of action property
                // Uri path
                request.getDescription(true),
                LocalDateTime.now()
        );


        // The object ResponseEntity is now built by two blocks: 1. status 2. body(error) 3. headers are omitted.
        return ResponseEntity.status(status).body(error);
    }

    // ==========================================================
    // 400 - BAD REQUEST (Client error types)
    // ==========================================================

    /*
     * If the exception comes from Bean Validation (@Valid),
     * extract a user-friendly validation message from the BindingResult.
     * Otherwise, use the message provided by the exception itself.
     */
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleBadRequest (Exception ex, WebRequest request) {

        // If the type of error is MethodArgumentNotValidException...
        String message = ex instanceof MethodArgumentNotValidException ?
                ((MethodArgumentNotValidException) ex)
                .getBindingResult()
                .getAllErrors()
                .get(0) // first validation error (design choice)
                .getDefaultMessage()

                // Else use this message
                : ex.getMessage();

        return buildError(HttpStatus.BAD_REQUEST,"Bad Request", message, request);

    }

    // ==========================================================
    // 401 - BAD CREDENTIALS
    // ==========================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED,"Unauthorized", "Invalid username or password", request);
    }

    // ==========================================================
    // 404 - NOT FOUND
    // ==========================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildError(HttpStatus.NOT_FOUND,"Resource not found", ex.getMessage(), request);
    }

    // ==========================================================
    // 405 - METHOD NOT ALLOWED
    // ==========================================================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildError(HttpStatus.METHOD_NOT_ALLOWED,"Method not allowed", ex.getMessage(),request);
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
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",ex.getMessage(), request);
    }

    // ==========================================================
    // 503 - SERVICE UNAVAILABLE
    // ==========================================================

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleServiceUnavailable(ServiceUnavailableException ex, WebRequest request) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE,"Service Unavailable", ex.getMessage(), request);
    }

}