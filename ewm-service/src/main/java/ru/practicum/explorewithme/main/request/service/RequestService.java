package ru.practicum.explorewithme.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.event.dal.EventRepository;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.event.model.EventState;
import ru.practicum.explorewithme.main.exception.ConflictException;
import ru.practicum.explorewithme.main.exception.NotFoundException;
import ru.practicum.explorewithme.main.request.dal.RequestRepository;
import ru.practicum.explorewithme.main.request.dto.RequestDto;
import ru.practicum.explorewithme.main.request.dto.RequestMapper;
import ru.practicum.explorewithme.main.request.dto.RequestStatusDto;
import ru.practicum.explorewithme.main.request.dto.RequestStatusUpdateDto;
import ru.practicum.explorewithme.main.request.model.Request;
import ru.practicum.explorewithme.main.request.model.RequestState;
import ru.practicum.explorewithme.main.user.dal.UserRepository;
import ru.practicum.explorewithme.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Optional<Request> existRequest = requestRepository.findByEventAndRequester(eventId, userId);
        if (existRequest.isPresent()) {
            throw new ConflictException("Пользователь уже подавал заявку на участие в данном событии");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь не может создать запрос на собственное мероприятие");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Мероприятие не опубликовано");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Превышено ограничение на количество участников мероприятия");
        }
        RequestState requestState = event.getParticipantLimit() == 0 || !event.getRequestModeration() ? RequestState.CONFIRMED : RequestState.PENDING;
        if (requestState.equals(RequestState.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        Request request = new Request();
        request.setRequester(user.getId());
        request.setEvent(eventId);
        request.setStatus(requestState);
        request.setCreated(LocalDateTime.now());

        return RequestMapper.fromRequest(requestRepository.save(request));
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        if (!request.getRequester().equals(userId)) {
            throw new NotFoundException("Пользователь не является автором заявки");
        }
        request.setStatus(RequestState.CANCELED);
        return RequestMapper.fromRequest(requestRepository.save(request));
    }

    public List<RequestDto> getRequestsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return requestRepository.findAllByRequester(userId).stream().map(RequestMapper::fromRequest).toList();
    }

    public List<RequestDto> getRequestsByEvent(Long eventId, Long userId) {
        return requestRepository.findAllByEvent(eventId).stream().map(RequestMapper::fromRequest).toList();
    }

    public RequestStatusDto updateRequestsStatus(Event event, Long userId, RequestStatusUpdateDto dto) {
        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());
        if (requests.size() != dto.getRequestIds().size()) {
            throw new NotFoundException("Не найдены заявки с идентификаторами: " + dto.getRequestIds());
        }

        int confirmedSize = event.getConfirmedRequests();
        int limit = event.getParticipantLimit();

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestState.PENDING)) {
                throw new ConflictException("Статус заявки уже подтвержден");
            }
            if (dto.getStatus().equals(RequestState.CONFIRMED)) {
                if (confirmedSize >= limit) {
                    throw new ConflictException("Превышено ограничение на количество участников мероприятия");
                } else {
                    event.setConfirmedRequests(++confirmedSize);
                }
            }
            request.setStatus(dto.getStatus());
        }
        requestRepository.saveAll(requests);
        eventRepository.save(event);

        return  RequestStatusDto.builder()
                .confirmedRequests(requests.stream().filter(request -> request.getStatus().equals(RequestState.CONFIRMED)).map(RequestMapper::fromRequest).toList())
                .rejectedRequests(requests.stream().filter(request -> request.getStatus().equals(RequestState.REJECTED)).map(RequestMapper::fromRequest).toList())
                .build();
    }
}
