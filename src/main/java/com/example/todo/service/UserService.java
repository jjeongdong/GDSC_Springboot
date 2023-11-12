package com.example.todo.service;

import com.example.todo.config.jwt.JwtUtils;
import com.example.todo.dto.UserDto;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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
    public String authenticate(String username, String password) throws AuthenticationException {
        User user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);

        if (user != null && (Objects.equals(user.getPassword(), password))) {
            // 인증 성공 시 JWT 토큰 생성 및 반환
            return jwtUtils.generateToken(username);
        } else {
            // 인증 실패 시 AuthenticationException 발생
            throw new RuntimeException("Authentication failed");
        }

    }


}
