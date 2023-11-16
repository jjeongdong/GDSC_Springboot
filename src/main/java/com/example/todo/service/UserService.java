package com.example.todo.service;

import com.example.todo.config.JwtFilter;
import com.example.todo.config.JwtTokenProvider;
import com.example.todo.dto.TokenResponse;
import com.example.todo.dto.UserRequestDto;
import com.example.todo.dto.UserResponseDto;
import com.example.todo.entity.RefreshToken;
import com.example.todo.entity.User;
import com.example.todo.repository.RefreshTokenRepository;
import com.example.todo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncryptionService passwordEncryptionService;


    @Transactional
    public UserResponseDto signup(UserRequestDto userRequestDto) {

        User user = User.builder()
                .username(userRequestDto.getUsername())
                .password(passwordEncryptionService.encrypt(userRequestDto.getPassword()))
                .build();

        userRepository.save(user);

        return UserResponseDto.builder()
                .username(userRequestDto.getUsername())
                .build();
    }

    @Transactional
    public TokenResponse login(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);

        if (user != null && Objects.equals(user.getPassword(), passwordEncryptionService.encrypt(password))) {

            Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findTopByUserOrderByIdDesc(user);


            if (optionalRefreshToken.isPresent()) {
                String DBRefreshToken = optionalRefreshToken.get().getToken();

                if (jwtTokenProvider.validateToken(DBRefreshToken) && DBRefreshToken != null) {
                    String accessToken = jwtTokenProvider.generateAccessToken(username);
                    return new TokenResponse(accessToken, DBRefreshToken);
                }
            }

            String accessToken = jwtTokenProvider.generateAccessToken(username);
            String refreshToken = jwtTokenProvider.generateRefreshToken(username);

            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .token(refreshToken)
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshTokenEntity);

            return new TokenResponse(accessToken, refreshToken);
        } else {
            throw new RuntimeException("Authentication failed");
        }
    }

}
