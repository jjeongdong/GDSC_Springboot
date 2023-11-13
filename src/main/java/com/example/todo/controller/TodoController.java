package com.example.todo.controller;

import com.example.todo.config.jwt.JwtUtils;
import com.example.todo.dto.PageResponseDto;
import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoListDto;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/list")
    public PageResponseDto list (
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestHeader("Authorization") String token
    ) {
        return todoService.findAll(pageNo, pageSize, sortBy, token);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoListDto> getTodoById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        TodoListDto todoListDto = todoService.findTodoById(id, token);
        return ResponseEntity.ok(todoListDto);
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody @Valid TodoDto todoDto, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(todoService.createTodo(todoDto, token));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodoById(@PathVariable Long id, @Valid @RequestBody TodoDto todoDto, @RequestHeader("Authorization") String token) {
        TodoDto updatedTodo = todoService.updateTodoById(id, todoDto, token);
        return ResponseEntity.ok(updatedTodo);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        todoService.deleteTodoById(id, token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<TodoListDto> complete(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        TodoListDto todoListDto = todoService.complete(id, token);
        return ResponseEntity.ok(todoListDto);
    }


}
