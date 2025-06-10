package ru.practicum.explorewithme.main.event.controller.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.main.event.dto.EventFullDto;
import ru.practicum.explorewithme.main.event.model.Sorting;
import ru.practicum.explorewithme.main.event.service.EventService;
import ru.practicum.explorewithme.statclient.client.StatClient;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
@Validated
public class EventPublicController {
    private final EventService eventService;
    private final StatClient statClient;

    @GetMapping
    public List<EventFullDto> getEventsPublic(@RequestParam(required = false) String text,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) Boolean paid,
                                              @RequestParam(required = false) String rangeStart,
                                              @RequestParam(required = false) String rangeEnd,
                                              @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                              @RequestParam(defaultValue = "EVENT_DATE") Sorting sort,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size,
                                              HttpServletRequest request) {
        log.info("Получен запрос на поиск событий с возможностью фильтрации");

        ResponseEntity<Object> actualResponse = statClient.save("ewm-service", request.getRequestURI(), request.getRemoteAddr());

        return eventService.getPublicEventsByParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventPublic(@PathVariable("eventId") Long eventId, HttpServletRequest request) {

        ResponseEntity<Object> actualResponse = statClient.save("ewm-service", request.getRequestURI(), request.getRemoteAddr());

        log.info("Получен запрос на поиск подробной информации об опубликованном событии: eventId = {}", eventId);
        return eventService.getEventPublic(eventId);
    }
}
