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
    @Size(min = 20, max = 2000, message = "Длина названия должна быть от 20 до 2000 символов")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;
    @PositiveOrZero(message = "Лимит участников должен быть положительным числом или равным нулю")
    private Integer participantLimit;
    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 3 до 120 символов")
    private String title;

    private StateAction stateAction;
}
