package com.example.todo.service;

import com.example.todo.config.jwt.JwtUtils;
import com.example.todo.dto.PageResponseDto;
import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoListDto;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    private User getUserFromToken(String token) {
        String username = jwtUtils.getUsernameFromToken(token);
        return userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public TodoDto createTodo(TodoDto todoDto, String token) {
        User user = getUserFromToken(token);

        Todo todo = Todo.builder()
                .title(todoDto.getTitle())
                .completed(false)
                .user(user)
                .build();

        todoRepository.save(todo);

        return todoDto;
    }

    @Transactional(readOnly = true)
    public PageResponseDto findAll(int pageNo, int pageSize, String sortBy, String token) {
        User user = getUserFromToken(token);

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        Page<Todo> todoPage = todoRepository.findAllByUser(pageable, user);

        List<Todo> todoList = todoPage.getContent();

        List<TodoListDto> content = todoList.stream()
                .map(todo -> TodoListDto.builder()
                        .title(todo.getTitle())
                        .completed(todo.getCompleted())
                        .build())
                .collect(Collectors.toList());


        return PageResponseDto.builder()
                .content(content)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(todoPage.getTotalElements())
                .totalPages(todoPage.getTotalPages())
                .last(todoPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public TodoListDto findTodoById(Long id, String token) {
        User user = getUserFromToken(token);

        Todo todo = todoRepository.findByIdAndUser(id, user).orElseThrow(EntityNotFoundException::new);

        return TodoListDto.builder()
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .build();
    }

    public TodoDto updateTodoById(Long id, TodoDto todoDto, String token) {
        User user = getUserFromToken(token);

        Todo todo = todoRepository.findByIdAndUser(id, user).orElseThrow(EntityNotFoundException::new);
        todo.setTitle(todoDto.getTitle());

        return TodoDto.builder()
                .title(todo.getTitle())
                .build();
    }


    public void deleteTodoById(Long id, String token) {
        User user = getUserFromToken(token);

        Todo todo = todoRepository.findByIdAndUser(id, user).orElseThrow(EntityNotFoundException::new);
        todoRepository.delete(todo);
    }

    public TodoListDto complete(Long id, String token) {
        User user = getUserFromToken(token);

        Todo todo = todoRepository.findByIdAndUser(id, user).orElseThrow(EntityNotFoundException::new);
        todo.setCompleted(true);

        return TodoListDto.builder()
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .build();
    }
}
