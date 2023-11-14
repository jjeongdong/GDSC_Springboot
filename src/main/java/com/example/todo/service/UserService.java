package com.example.todo.service;

import com.example.todo.config.JwtUtils;
import com.example.todo.dto.AuthResponse;
import com.example.todo.dto.UserDto;
import com.example.todo.entity.RefreshToken;
import com.example.todo.entity.User;
import com.example.todo.repository.RefreshTokenRepository;
import com.example.todo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;


    @Transactional
    public UserDto signup(UserDto userDto) {

        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .build();

        userRepository.save(user);

        return userDto;
    }

    @Transactional
    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);

        if (user != null && Objects.equals(user.getPassword(), password)) {
            String accessToken = jwtUtils.generateAccessToken(username);
            String refreshToken = jwtUtils.generateRefreshToken(username);

            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setUser(user);

            refreshTokenRepository.save(refreshTokenEntity);

            return new AuthResponse(accessToken, refreshToken);
        } else {
            throw new RuntimeException("Authentication failed");
        }

    }


}
