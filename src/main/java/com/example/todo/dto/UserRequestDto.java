package com.example.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 9, max = 50)
    private String password;
}
