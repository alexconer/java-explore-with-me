package ru.practicum.explorewithme.main.comment.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.main.comment.dto.CommentAcceptDto;
import ru.practicum.explorewithme.main.comment.dto.CommentFullDto;
import ru.practicum.explorewithme.main.comment.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/comments")
@Validated
public class CommentAdminController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentFullDto updateCommentAdmin(@PathVariable("commentId") Long commentId,
                                             @Valid @RequestBody CommentAcceptDto dto) {
        log.info("Получен запрос на изменение статуса комментария с id = {}", commentId);
        return commentService.updateCommentAdmin(commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentAdmin(@PathVariable("commentId") Long commentId) {
        log.info("Получен запрос на удаление комментария с id = {}", commentId);
        commentService.deleteCommentAdmin(commentId);
    }

    @GetMapping("/{commentId}")
    public CommentFullDto getCommentByIdAdmin(@PathVariable("commentId") Long commentId) {
        log.info("Получен запрос на получение комментария с id = {}", commentId);
        return commentService.getCommentByIdAdmin(commentId);
    }

    @GetMapping
    public List<CommentFullDto> getAllCommentsAdmin(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на получение списка комментариев со смещением от {} на {} позиций", from, size);
        return commentService.getAllCommentsAdmin(from, size);
    }
}
