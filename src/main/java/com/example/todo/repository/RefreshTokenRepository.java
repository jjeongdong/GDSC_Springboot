package com.example.todo.repository;

import com.example.todo.entity.RefreshToken;
import com.example.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findTopByUserOrderByIdDesc(User user);
}
