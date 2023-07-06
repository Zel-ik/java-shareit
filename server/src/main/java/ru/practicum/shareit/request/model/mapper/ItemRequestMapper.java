package ru.practicum.shareit.request.model.mapper;

import ru.practicum.shareit.entityMapper.EntityMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;

public interface ItemRequestMapper extends EntityMapper<ItemRequestDto, ItemRequest> {
    ItemRequest mapFrom(ItemRequestDto i, Long userId);
}
