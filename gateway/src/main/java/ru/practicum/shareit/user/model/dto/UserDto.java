package ru.practicum.shareit.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private final String name;
    @NotBlank(message = "Email пользователя не может быть пустым")
    @Email(message = "Email пользователя должен быть корректным")
    private final String email;
}
