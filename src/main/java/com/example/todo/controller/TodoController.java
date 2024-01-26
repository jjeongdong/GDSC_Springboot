package com.example.todo.controller;

import com.example.todo.dto.PageResponseDto;
import com.example.todo.dto.TodoDto;
import com.example.todo.dto.TodoListDto;
import com.example.todo.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/list")
    public PageResponseDto list(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return todoService.findAll(pageNo, pageSize, sortBy, request, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoListDto> getTodoById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        TodoListDto todoListDto = todoService.findTodoById(id, request, response);
        return ResponseEntity.ok(todoListDto);
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody @Valid TodoDto todoDto, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(todoService.createTodo(todoDto, request, response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodoById(@PathVariable Long id, @Valid @RequestBody TodoDto todoDto, HttpServletRequest request, HttpServletResponse response) {
        TodoDto updatedTodo = todoService.updateTodoById(id, todoDto, request, response);
        return ResponseEntity.ok(updatedTodo);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        todoService.deleteTodoById(id, request, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<TodoListDto> complete(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        TodoListDto todoListDto = todoService.completeTodo(id, request, response);
        return ResponseEntity.ok(todoListDto);
    }


}
