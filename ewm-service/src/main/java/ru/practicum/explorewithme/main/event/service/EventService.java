package ru.practicum.explorewithme.main.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.category.dal.CategoryRepository;
import ru.practicum.explorewithme.main.category.model.Category;
import ru.practicum.explorewithme.main.event.dal.EventRepository;
import ru.practicum.explorewithme.main.event.dto.EventFullDto;
import ru.practicum.explorewithme.main.event.dto.EventMapper;
import ru.practicum.explorewithme.main.event.dto.EventCreateDto;
import ru.practicum.explorewithme.main.event.dto.EventShortDto;
import ru.practicum.explorewithme.main.event.dto.EventUpdateDto;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.event.model.EventState;
import ru.practicum.explorewithme.main.event.model.Sorting;
import ru.practicum.explorewithme.main.event.model.StateAction;
import ru.practicum.explorewithme.main.exception.BadRequestException;
import ru.practicum.explorewithme.main.exception.ConflictException;
import ru.practicum.explorewithme.main.exception.NotFoundException;
import ru.practicum.explorewithme.main.request.dto.RequestDto;
import ru.practicum.explorewithme.main.request.dto.RequestStatusDto;
import ru.practicum.explorewithme.main.request.dto.RequestStatusUpdateDto;
import ru.practicum.explorewithme.main.request.service.RequestService;
import ru.practicum.explorewithme.main.user.dal.UserRepository;
import ru.practicum.explorewithme.main.user.model.User;
import ru.practicum.explorewithme.statclient.client.StatClient;
import ru.practicum.explorewithme.statdto.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestService requestService;
    private final StatClient statClient;

    @Value("${app.event.minHoursBeforeEventCreation}")
    private int minHoursBeforeEventCreation;
    @Value("${app.event.minHoursBeforeEventUpdate}")
    private int minHoursBeforeEventUpdate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public EventFullDto createEvent(Long userId, EventCreateDto reqDto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Category category = categoryRepository.findById(reqDto.getCategory()).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + reqDto.getCategory()));

        Event event = EventMapper.toEvent(reqDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreated(LocalDateTime.now());

        if (event.getCreated().plusHours(minHoursBeforeEventCreation).isAfter(event.getEventDate())) {
            throw new BadRequestException("Время начала мероприятия должно быть не раньше чем через " + minHoursBeforeEventCreation + " часа");
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, EventUpdateDto reqDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("У пользователя " + userId + " нет доступа к событию " + eventId);
        }
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Невозможно изменить опубликованное мероприятие");
        }
        if (reqDto.getAnnotation() != null) {
            event.setAnnotation(reqDto.getAnnotation());
        }
        if (reqDto.getCategory() != null) {
            Category category = categoryRepository.findById(reqDto.getCategory()).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + reqDto.getCategory()));
            event.setCategory(category);
        }
        if (reqDto.getDescription() != null) {
            event.setDescription(reqDto.getDescription());
        }
        if (reqDto.getEventDate() != null) {
            if (LocalDateTime.now().plusHours(minHoursBeforeEventCreation).isAfter(reqDto.getEventDate())) {
                throw new BadRequestException("Время начала мероприятия должно быть не раньше чем через " + minHoursBeforeEventCreation + " часа");
            }
            event.setEventDate(reqDto.getEventDate());
        }
        if (reqDto.getPaid() != null) {
            event.setPaid(reqDto.getPaid());
        }
        if (reqDto.getParticipantLimit() != null) {
            event.setParticipantLimit(reqDto.getParticipantLimit());
        }
        if (reqDto.getRequestModeration() != null) {
            event.setRequestModeration(reqDto.getRequestModeration());
        }
        if (reqDto.getTitle() != null) {
            event.setTitle(reqDto.getTitle());
        }
        if (reqDto.getLocation() != null) {
            event.setLocationLat(reqDto.getLocation().getLat());
            event.setLocationLon(reqDto.getLocation().getLon());
        }
        if (reqDto.getStateAction() != null) {

            event.setState(switch (reqDto.getStateAction()) {
                case SEND_TO_REVIEW -> EventState.PENDING;
                case CANCEL_REVIEW -> EventState.CANCELED;
                default -> throw new IllegalStateException("Unexpected value: " + reqDto.getStateAction());
            });
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    public List<EventShortDto> getAllUserEvents(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Page<Event> pagedResult = eventRepository.findByInitiator(user, pageable);
        return pagedResult.stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    public EventFullDto getEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.getEventById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Не найдено событие с идентификатором " + eventId);
        }

        return EventMapper.toEventFullDto(event);
    }

    public List<RequestDto> getEventRequests(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));
        return requestService.getRequestsByEvent(eventId, userId);
    }

    @Transactional
    public RequestStatusDto updateEventRequests(Long userId, Long eventId, RequestStatusUpdateDto dto) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Не найдено событие с идентификатором " + eventId);
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return null;
        }

        return requestService.updateRequestsStatus(event, userId, dto);
    }

    public List<EventFullDto> getEventsByParams(List<Long> users, List<Long> categories, List<String> states, String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime dateFrom = null;
        if (rangeStart != null) {
            dateFrom = LocalDateTime.parse(rangeStart, formatter);
        }
        LocalDateTime dateTo = null;
        if (rangeEnd != null) {
            dateTo = LocalDateTime.parse(rangeEnd, formatter);
        }

        if (categories != null) {
            List<Category> existCategory = categoryRepository.findAllByIdIn(categories);
            if (existCategory != null && existCategory.isEmpty()) {
                throw new BadRequestException("Не найдена категория с идентификатором " + categories);
            }
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        return eventRepository.getEventsByFilter(users, categories, states, dateFrom, dateTo, pageable).stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, EventUpdateDto dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException("Не найдена категория с идентификатором" + dto.getCategory()));
            event.setCategory(category);
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            if (LocalDateTime.now().plusHours(minHoursBeforeEventUpdate).isAfter(dto.getEventDate())) {
                throw new BadRequestException("Время начала мероприятия должно быть не раньше чем через " + minHoursBeforeEventUpdate + " часа");
            }
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getLocation() != null) {
            event.setLocationLat(dto.getLocation().getLat());
            event.setLocationLon(dto.getLocation().getLon());
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case StateAction.PUBLISH_EVENT:
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublished(LocalDateTime.now());
                    break;
                case StateAction.REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано");
                    }
                    event.setState(EventState.CANCELED);
                    event.setPublished(null);
                    break;
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    public List<EventFullDto> getPublicEventsByParams(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, Sorting sort, Integer from, Integer size) {
        LocalDateTime dateFrom = null;
        if (rangeStart != null) {
            dateFrom = LocalDateTime.parse(rangeStart, formatter);
        }
        LocalDateTime dateTo = null;
        if (rangeEnd != null) {
            dateTo = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (rangeStart == null && rangeEnd == null) {
            dateFrom = LocalDateTime.now();
        }
        if (text != null) {
            text = text.toLowerCase();
        }
        if (categories != null) {
            List<Category> existCategory = categoryRepository.findAllByIdIn(categories);
            if (existCategory != null && existCategory.isEmpty()) {
                throw new BadRequestException("Не найдена категория с идентификатором " + categories);
            }
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<Event> events = sort == Sorting.EVENT_DATE ?
                eventRepository.getPublicEventsByParamsSortedByEventDate(text, categories, paid, dateFrom, dateTo, onlyAvailable, pageable) :
                eventRepository.getEventsByParamsSortedByViews(text, categories, paid, dateFrom, dateTo, onlyAvailable, pageable);

        return events.stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    public EventFullDto getEventPublic(Long eventId) {
        Event event = eventRepository.getEventById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Не найдено событие с идентификатором " + eventId);
        }

        String[] uris = {"/events/" + eventId};
        ResponseEntity<Object> actualResponse = statClient.getStats(LocalDateTime.of(1900, 1, 1, 0, 0), LocalDateTime.now(), uris, true);

        if (actualResponse.getStatusCode() == HttpStatus.OK && actualResponse.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            StatDto[] stats = mapper.convertValue(actualResponse.getBody(), StatDto[].class);
            event.setViews(stats != null && stats.length != 0 ? stats[0].getHits() : 0);
            eventRepository.save(event);
        }

        return EventMapper.toEventFullDto(event);
    }
}
