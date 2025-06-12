package ru.practicum.explorewithme.main.comment.controller.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.main.comment.dto.CommentShortDto;
import ru.practicum.explorewithme.main.comment.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("events/{eventId}/comments")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentShortDto> getAllComments(@PathVariable("eventId") Long eventId) {
        log.info("Получен запрос на получение всех комментариев к событию с id = {}", eventId);
        return commentService.getAllCommentsByEventId(eventId);
    }
}
