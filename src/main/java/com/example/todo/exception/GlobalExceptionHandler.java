package com.example.todo.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> dtoValidation(final MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler({ServiceException.class, NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Object> handleException(Exception e) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = determineHttpStatus(e);

        Map<String, Object> errors = new HashMap<>();
        errors.put("Status", getStatus(e));
        errors.put("ErrorMessage", getErrorMessage(e));
        errors.put("Date", String.valueOf(new Date()));

        return new ResponseEntity<>(errors, headers, status);
    }

    private HttpStatus determineHttpStatus(Exception e) {
        if (e instanceof ServiceException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (e instanceof NoHandlerFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return HttpStatus.METHOD_NOT_ALLOWED;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String getStatus(Exception e) {
        if (e instanceof ServiceException) {
            return ((ServiceException) e).getExceptionStatus().getStatus();
        } else if (e instanceof NoHandlerFoundException) {
            return String.valueOf(HttpStatus.NOT_FOUND);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return String.valueOf(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return null;
    }

    private String getErrorMessage(Exception e) {
        if (e instanceof ServiceException) {
            return ((ServiceException) e).getExceptionStatus().getMessage();
        } else if (e instanceof NoHandlerFoundException) {
            return "해당 요청을 찾을 수 없습니다.";
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return "지원되지 않는 HTTP 메서드입니다.";
        }

        return "Internal Server Error";
    }

}