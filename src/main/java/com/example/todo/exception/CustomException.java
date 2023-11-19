package com.example.todo.exception;

public class CustomException extends RuntimeException {

    private final ExceptionStatus exceptionStatus;

    public CustomException(ExceptionStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public ExceptionStatus getExceptionStatus() {
        return exceptionStatus;
    }
}
