package com.example.todo.service;

import com.example.todo.config.jwt.JwtUtils;
import com.example.todo.dto.RefreshTokenResponse;
import com.example.todo.entity.RefreshToken;
import com.example.todo.entity.User;
import com.example.todo.repository.RefreshTokenRepository;
import com.example.todo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenResponse refreshAccessToken(String refreshToken) {
        if (jwtUtils.validateToken(refreshToken)) {

            String username = jwtUtils.getUsernameFromRefreshToken(refreshToken);

            // 데이터베이스에서 사용자 정보 가져오기
            User user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);

            RefreshToken DBRefreshToken = refreshTokenRepository.findTopByUserOrderByIdDesc(user).orElseThrow(EntityNotFoundException::new);

            if (Objects.equals(refreshToken, DBRefreshToken.getToken())) {

                return RefreshTokenResponse
                        .builder()
                        .accessToken(jwtUtils.generateAccessToken(username))
                        .build();

            } else {
                throw new RuntimeException("Invalid Refresh Token");
            }
        }
        throw new RuntimeException("Invalid Refresh Token");
    }
}
