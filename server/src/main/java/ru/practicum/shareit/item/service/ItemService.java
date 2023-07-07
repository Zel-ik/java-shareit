package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    void deleteItem(Long userId, Long itemId);

    ItemBookingDto getItemById(Long userId, Long itemId);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

    List<ItemBookingDto> getUserItems(Long userId, Integer from, Integer size);

    List<ItemDto> searchItem(String text, Integer from, Integer size);
}
