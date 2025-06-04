package ru.practicum.explorewithme.main.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReqDto {

    @Size(min = 2, message = "Минимальная длина поля 20 символов")
    @Size(max = 250, message = "Максимальная длина поля 2000 символов")
    @NotNull(message = "Имя пользователя не может быть пустым")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @NotEmpty(message = "Email пользователя не может быть пустым")
    @Size(min = 6, message = "Минимальная длина поля 20 символов")
    @Size(max = 254, message = "Максимальная длина поля 2000 символов")
    @Email(message = "Некорректный email")
    private String email;
}
