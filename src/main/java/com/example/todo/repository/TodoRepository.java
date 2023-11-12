package com.example.todo.repository;

import com.example.todo.dto.TodoListDto;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findAllByUser(Pageable pageable, User user);

    Optional<Todo> findByIdAndUser(Long id, User user);
}
