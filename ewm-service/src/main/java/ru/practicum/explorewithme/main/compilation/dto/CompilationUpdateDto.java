package ru.practicum.explorewithme.main.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompilationUpdateDto {
    @Size(min = 1, message = "Минимальная длина поля 20 символов")
    @Size(max = 50, message = "Максимальная длина поля 2000 символов")
    private String title;

    private Boolean pinned = false;

    private List<Long> events;
}
