package ru.practicum.shareit.item.model.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;

public interface ItemMapper {

    ItemDto mapTo(Item entity, long userId);

    Item mapFrom(ItemDto entity, long userId);

    Item mapFrom(ItemDto itemDto, Item item);
}
