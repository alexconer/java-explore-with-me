package ru.practicum.explorewithme.main.comment.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.main.comment.dto.CommentCreateDto;
import ru.practicum.explorewithme.main.comment.dto.CommentFullDto;
import ru.practicum.explorewithme.main.comment.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createCommentPrivate(@PathVariable("userId") Long userId,
                                 @PathVariable("eventId") Long eventId,
                                 @Valid @RequestBody CommentCreateDto dto) {
        log.info("Получен запрос на создание комментария пользователя с id = {} к событию с id = {}, данные комментария: {}", userId, eventId, dto);
        return commentService.createComment(userId, eventId, dto);
    }

    @PatchMapping("/{commentId}")
    public CommentFullDto updateCommentPrivate(@PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId,
                                               @PathVariable("commentId") Long commentId,
                                               @Valid @RequestBody CommentCreateDto dto) {
        log.info("Получен запрос на обновление комментария пользователя с id = {} к событию с id = {}, данные комментария: {}", userId, eventId, dto);
        return commentService.updateComment(userId, eventId, commentId, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable("userId") Long userId,
                              @PathVariable("eventId") Long eventId,
                              @PathVariable("commentId") Long commentId) {
        log.info("Получен запрос на удаление комментария id = {} от пользователя с id = {} к событию с id = {}", commentId, userId, eventId);
        commentService.deleteComment(userId, eventId, commentId);
    }

    @GetMapping
    public List<CommentFullDto> findAllComments(@PathVariable("userId") Long userId,
                                                @PathVariable("eventId") Long eventId) {
        log.info("Получен запрос на получение всех комментариев пользователя с id = {} к событию с id = {}", userId, eventId);
        return commentService.getAllCommentsByUser(userId, eventId);
    }

    @GetMapping("/{commentId}")
    public CommentFullDto findCommentById(@PathVariable("userId") Long userId,
                                          @PathVariable("eventId") Long eventId,
                                          @PathVariable("commentId") Long commentId) {
        log.info("Получен запрос на получение комментария пользователя с id = {} к событию с id = {}", userId, eventId);
        return commentService.getCommentById(userId, eventId, commentId);
    }
}
