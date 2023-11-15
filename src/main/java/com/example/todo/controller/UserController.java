package com.example.todo.controller;

import com.example.todo.dto.AuthResponse;
import com.example.todo.dto.UserDto;
import com.example.todo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody @Valid UserDto userDto) {
        UserDto saveduserDto = userService.signup(userDto);
        return ResponseEntity.ok(saveduserDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid UserDto userDto) {
        AuthResponse authResponse = userService.login(userDto.getUsername(), userDto.getPassword());
        return ResponseEntity.ok(authResponse);
    }

}
