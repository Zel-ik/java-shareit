package ru.practicum.shareit.item.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;//ссылка на запрос пользователя о создании
}
