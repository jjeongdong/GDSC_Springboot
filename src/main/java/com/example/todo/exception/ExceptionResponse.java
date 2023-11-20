package com.example.todo.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String status;
    private final String errorMessage;


    public ExceptionResponse(String status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}