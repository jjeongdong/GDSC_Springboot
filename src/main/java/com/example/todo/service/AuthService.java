package com.example.todo.service;

import com.example.todo.config.JwtUtils;
import com.example.todo.dto.RefreshTokenResponse;
import com.example.todo.entity.RefreshToken;
import com.example.todo.entity.User;
import com.example.todo.repository.RefreshTokenRepository;
import com.example.todo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenResponse refreshAccessToken(HttpServletRequest httpServletRequest) {

        String refreshToken = httpServletRequest.getHeader("Refresh-Token");

        if (jwtUtils.validateToken(refreshToken)) {

            String username = jwtUtils.getUsernameFromRefreshToken(refreshToken);
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
