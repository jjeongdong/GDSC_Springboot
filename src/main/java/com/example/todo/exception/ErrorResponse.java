package com.example.todo.exception;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String status;
    private final String errorMessage;


    public ErrorResponse(String status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}