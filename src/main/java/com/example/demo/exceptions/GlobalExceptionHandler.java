package com.example.demo.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(
        UnauthorizedException exception
    ) {
        return buildResponse(
            HttpStatus.UNAUTHORIZED,
            exception.getMessage()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(
        ConflictException exception
    ) {
        return buildResponse(
            HttpStatus.CONFLICT,
            exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
        MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error ->
                error.getField() + ": " + error.getDefaultMessage()
            )
            .collect(Collectors.joining("; "));

        return buildResponse(
            HttpStatus.BAD_REQUEST,
            message
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
        ConstraintViolationException exception
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
        IllegalArgumentException exception
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
        );
    }

    private ResponseEntity<ApiError> buildResponse(
        HttpStatus status,
        String message
    ) {
        ApiError error = new ApiError(
            status.value(),
            status.getReasonPhrase(),
            message,
            LocalDateTime.now()
        );

        return ResponseEntity
            .status(status)
            .body(error);
    }
}