package ru.practicum.shareit.request.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDtoOut {
    private long id;
    private String description;
    private UserDto requester; //кто запрашивает
    private LocalDateTime created;
    private List<ItemDto> items;
}