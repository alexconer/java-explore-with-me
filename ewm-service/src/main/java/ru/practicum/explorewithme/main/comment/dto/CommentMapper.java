package ru.practicum.explorewithme.main.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.comment.model.Comment;
import ru.practicum.explorewithme.main.event.dto.EventMapper;
import ru.practicum.explorewithme.main.user.dto.UserMapper;

@UtilityClass
public class CommentMapper {
    public CommentFullDto toCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .userMessage(comment.getUserMessage())
                .adminMessage(comment.getAdminMessage())
                .accepted(comment.isAccepted())
                .createdOn(comment.getCreated())
                .build();
    }

    public CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .userMessage(comment.getUserMessage())
                .createdOn(comment.getCreated())
                .build();
    }

    public Comment toComment(CommentCreateDto dto) {
        Comment comment = new Comment();
        comment.setUserMessage(dto.getMessage());
        return comment;
    }
}
