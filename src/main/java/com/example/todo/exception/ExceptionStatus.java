package com.example.todo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ExceptionStatus {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found"),
    DUPLICATE_USER_ID(HttpStatus.BAD_REQUEST, "이미 사용 중인 회원 아이디입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public String getStatus() {
        return name();
    }
}
