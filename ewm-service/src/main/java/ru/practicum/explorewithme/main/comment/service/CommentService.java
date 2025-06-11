package ru.practicum.explorewithme.main.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.comment.dal.CommentRepository;
import ru.practicum.explorewithme.main.comment.dto.CommentAcceptDto;
import ru.practicum.explorewithme.main.comment.dto.CommentCreateDto;
import ru.practicum.explorewithme.main.comment.dto.CommentFullDto;
import ru.practicum.explorewithme.main.comment.dto.CommentMapper;
import ru.practicum.explorewithme.main.comment.dto.CommentShortDto;
import ru.practicum.explorewithme.main.comment.model.Comment;
import ru.practicum.explorewithme.main.event.dal.EventRepository;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.exception.BadRequestException;
import ru.practicum.explorewithme.main.exception.NotFoundException;
import ru.practicum.explorewithme.main.user.dal.UserRepository;
import ru.practicum.explorewithme.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentFullDto createComment(Long userId, Long eventId, CommentCreateDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));
        Comment comment = CommentMapper.toComment(dto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setAccepted(false);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentFullDto updateComment(Long userId, Long eventId, Long commentId, CommentCreateDto dto) {
        checkCommentAuthor(userId, eventId, commentId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Не найден комментарий с идентификатором " + commentId));

        comment.setUserMessage(dto.getMessage());
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        checkCommentAuthor(userId, eventId, commentId);

        commentRepository.deleteById(commentId);
    }


    public List<CommentFullDto> getAllCommentsByUser(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));

        return commentRepository.findAllByAuthorIdAndEventId(userId, eventId).stream()
                .map(CommentMapper::toCommentFullDto)
                .toList();
    }


    public CommentFullDto getCommentById(Long userId, Long eventId, Long commentId) {
        checkCommentAuthor(userId, eventId, commentId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Не найден комментарий с идентификатором " + commentId));

        return CommentMapper.toCommentFullDto(comment);
    }

    @Transactional
    public CommentFullDto updateCommentAdmin(Long commentId, CommentAcceptDto dto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Не найден комментарий с идентификатором " + commentId));
        if (comment.isAccepted()) {
            throw new BadRequestException("Комментарий уже опубликован");
        }
        if (dto.getMessage() != null) {
            comment.setAdminMessage(dto.getMessage());
        }
        comment.setAccepted(dto.getAccepted());

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Не найден комментарий с идентификатором " + commentId));
        commentRepository.deleteById(commentId);
    }

    public CommentFullDto getCommentByIdAdmin(Long commentId) {
        return CommentMapper.toCommentFullDto(commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Не найден комментарий с идентификатором " + commentId)));
    }

    public List<CommentFullDto> getAllCommentsAdmin(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Page<Comment> page = commentRepository.findAllNotAccepted(pageable);
        return page.getContent().stream().map(CommentMapper::toCommentFullDto).toList();
    }

    public List<CommentShortDto> getAllCommentsByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));
        return commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper::toCommentShortDto)
                .toList();
    }

    private void checkCommentAuthor(Long userId, Long eventId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором " + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено событие с идентификатором " + eventId));

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Не найден комментарий с идентификатором " + commentId));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new BadRequestException("Вы не являетесь автором комментария");
        }
        if (comment.isAccepted()) {
            throw new BadRequestException("Вы не можете удалить опубликованный комментарий");
        }
    }
}
