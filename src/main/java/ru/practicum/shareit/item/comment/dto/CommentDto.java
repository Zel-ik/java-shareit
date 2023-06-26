package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validationgroup.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private long id;
    @NotBlank(groups = Create.class, message = "Поле Комментарий не должно быть пустым")
    @Size(max = 255)
    private String text;
    private String authorName;
    private LocalDateTime created;
}