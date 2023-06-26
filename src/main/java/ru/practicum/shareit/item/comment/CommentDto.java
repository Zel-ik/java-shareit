package ru.practicum.shareit.item.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}