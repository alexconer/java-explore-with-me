package ru.practicum.explorewithme.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.main.event.model.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventCreateDto {
    @Size(min = 20, message = "Минимальная длина поля 20 символов")
    @Size(max = 2000, message = "Максимальная длина поля 2000 символов")
    @NotNull(message = "Краткое описание не может быть пустым")
    @NotBlank(message = "Краткое описание не может быть пустым")
    private String annotation;

    @NotNull(message = "Категория не может быть пустым")
    private Long category;

    @Size(min = 20, message = "Минимальная длина поля 20 символов")
    @Size(max = 7000, message = "Максимальная длина поля 7000 символов")
    @NotNull(message = "Описание не может быть пустым")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Дата события не может быть пустым")
    private LocalDateTime eventDate;

    @NotNull(message = "Местоположение не может быть пустым")
    private Location location;

    private Boolean paid;
    @PositiveOrZero(message = "Лимит участников должен быть положительным числом или равным нулю")
    private Integer participantLimit;
    private Boolean requestModeration;

    @Size(min = 3, message = "Минимальная длина поля 3 символов")
    @Size(max = 120, message = "Максимальная длина поля 120 символов")
    @NotNull(message = "Заголовок не может быть пустым")
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;
}
