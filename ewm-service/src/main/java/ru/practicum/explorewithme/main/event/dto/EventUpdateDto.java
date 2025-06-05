package ru.practicum.explorewithme.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.main.event.model.Location;
import ru.practicum.explorewithme.main.event.model.StateAction;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventUpdateDto {
    @Size(min = 20, message = "Минимальная длина поля аннотация 20 символов")
    @Size(max = 2000, message = "Максимальная длина поля аннотация 2000 символов")
    private String annotation;

    private Long category;

    @Size(min = 20, message = "Минимальная длина поля описание 20 символов")
    @Size(max = 7000, message = "Максимальная длина поля описание 7000 символов")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;
    @PositiveOrZero(message = "Лимит участников должен быть положительным числом или равным нулю")
    private Integer participantLimit;
    private Boolean requestModeration;

    @Size(min = 3, message = "Минимальная длина поля заголовок 3 символов")
    @Size(max = 120, message = "Максимальная длина поля заголовок 120 символов")
    private String title;

    private StateAction stateAction;
}
