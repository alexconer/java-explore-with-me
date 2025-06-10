package ru.practicum.explorewithme.main.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompilationUpdateDto {
    @Size(min = 1, max = 50, message = "Длина названия должна быть от 1 до 50 символов")
    private String title;

    private Boolean pinned = false;

    private List<Long> events;
}
