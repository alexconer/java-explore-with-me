package ru.practicum.explorewithme.main.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {
    private Long id;
    @Size(min = 1, max = 50, message = "Длина названия не должна быть от 1 до 50 символов")
    @NotNull(message = "Название категории не может быть пустым")
    @NotBlank(message = "Название категории не может быть пустым")
    private String name;
}
