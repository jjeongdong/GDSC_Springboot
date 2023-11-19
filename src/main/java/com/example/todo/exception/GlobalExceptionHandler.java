package com.example.todo.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> dtoValidation(final MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error)-> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler({IllegalArgumentException.class, EntityNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleException(RuntimeException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본적으로 500 Internal Server Error
        if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }

        Map<String, String> error = new HashMap<>();
        error.put("status", status.toString());
        error.put("message", ex.getMessage());

        return ResponseEntity.status(status)
                .body(error);
    }



}