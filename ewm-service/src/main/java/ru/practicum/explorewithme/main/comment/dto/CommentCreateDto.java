package ru.practicum.explorewithme.main.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentCreateDto {
    @Size(min = 5, max = 7000, message = "Комментарий должен быть от 5 до 7000 символов")
    @NotBlank(message = "Комментарий не может быть пустым")
    private String message;
}
