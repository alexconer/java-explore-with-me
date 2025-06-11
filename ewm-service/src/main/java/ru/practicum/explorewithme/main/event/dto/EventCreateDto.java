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
    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов")
    @NotBlank(message = "Краткое описание не может быть пустым")
    private String annotation;

    @NotNull(message = "Категория не может быть пустым")
    private Long category;

    @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")
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

    @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 3 до 120 символов")
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;
}
