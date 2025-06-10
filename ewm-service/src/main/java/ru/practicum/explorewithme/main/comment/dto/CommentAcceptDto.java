package ru.practicum.explorewithme.main.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentAcceptDto {
    private Boolean accepted;
    private String message;
}
