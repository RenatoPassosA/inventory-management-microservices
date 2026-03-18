package com.inventory.inventory.adapter.input.handler;

import com.inventory.inventory.adapter.input.controller.response.ErrorResponse;
import com.inventory.inventory.domain.exception.InventoryAlreadyExistsException;
import com.inventory.inventory.domain.exception.InventoryInsufficientStockException;
import com.inventory.inventory.domain.exception.InventoryNotFoundException;
import com.inventory.inventory.domain.exception.InvalidInventoryMovementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInventoryNotFoundException(
            InventoryNotFoundException ex
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                List.of(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(InventoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleInventoryAlreadyExistsException(
            InventoryAlreadyExistsException ex
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                List.of(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(InventoryInsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInventoryInsufficientStockException(
            InventoryInsufficientStockException ex
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                List.of(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(InvalidInventoryMovementException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInventoryMovementException(
            InvalidInventoryMovementException ex
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                List.of(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                errors,
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Unexpected internal server error",
                List.of(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }
}