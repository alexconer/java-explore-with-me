package ru.practicum.explorewithme.main.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReqDto {

    @Size(min = 2, max = 250, message = "Длина имени должна быть от 2 до 250 символов")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @NotEmpty(message = "Email пользователя не может быть пустым")
    @Size(min = 6, max = 254, message = "Длина email-адреса должна быть от 6 до 254 символов")
    @Email(message = "Некорректный email")
    private String email;
}
