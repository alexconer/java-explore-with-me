package ru.practicum.explorewithme.main.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class CompilationCreateDto {
    @Size(min = 1, max = 50, message = "Длина названия должна быть от 1 до 50 символов")
    @NotBlank(message = "Название подборки не может быть пустым")
    private String title;

    private Boolean pinned = false;

    private List<Long> events;
}
