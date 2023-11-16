package com.example.todo.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

}
