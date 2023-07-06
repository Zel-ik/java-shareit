package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItem(long itemId, long userId);

    List<ItemDto> getItems(long userId);

    List<ItemDto> getItemsByText(String text, long userId);

    CommentDto createComment(CommentDto commentDto, long userId, long itemId);
}
