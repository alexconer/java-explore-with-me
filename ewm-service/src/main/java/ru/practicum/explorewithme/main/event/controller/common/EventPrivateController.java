package ru.practicum.explorewithme.main.event.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.main.event.dto.EventFullDto;
import ru.practicum.explorewithme.main.event.dto.EventCreateDto;
import ru.practicum.explorewithme.main.event.dto.EventShortDto;
import ru.practicum.explorewithme.main.event.dto.EventUpdateDto;
import ru.practicum.explorewithme.main.event.service.EventService;
import ru.practicum.explorewithme.main.request.dto.RequestDto;
import ru.practicum.explorewithme.main.request.dto.RequestStatusDto;
import ru.practicum.explorewithme.main.request.dto.RequestStatusUpdateDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventPrivate(@PathVariable("userId") Long userId,
                                           @Valid @RequestBody EventCreateDto dto) {
        log.info("Получен запрос на создание события: userId={}, dto={}", userId, dto);
        return eventService.createEvent(userId, dto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventPrivate(@PathVariable("userId") Long userId,
                                           @PathVariable("eventId") Long eventId,
                                           @Valid @RequestBody EventUpdateDto dto) {
        log.info("Получен запрос на изменение события: userId={}, eventId={}, dto={}", userId, eventId, dto);
        return eventService.updateEvent(userId, eventId, dto);
    }

    @GetMapping
    public List<EventShortDto> getUserEventsPrivate(@PathVariable("userId") Long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на поиск событий: userId={}", userId);
        return eventService.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventPrivate(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId) {
        log.info("Получен запрос на поиск события: userId={}, eventId={}", userId, eventId);
        return eventService.getEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEventRequestsPrivate(@PathVariable("userId") Long userId,
                                                    @PathVariable("eventId") Long eventId) {
        log.info("Получен запрос на поиск информации о запросах на участие в событии: userId={}, eventId={}", userId, eventId);
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusDto updateEventRequestsPrivate(@PathVariable("userId") Long userId,
                                                       @PathVariable("eventId") Long eventId,
                                                       @RequestBody RequestStatusUpdateDto dto) {
        log.info("Получен запрос на изменение заявок на участие в событии: userId={}, eventId={}, dto={}", userId, eventId, dto);
        return eventService.updateEventRequests(userId, eventId, dto);
    }
}
