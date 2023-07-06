package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentOutDto;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(ItemDto item, Long userId);

    ItemDto updateItem(ItemDto item, long itemId, long userId);

    ItemBookingDto getItemById(long id, long userId);

    Collection<ItemBookingDto> getAllItem(long userId);

    Collection<ItemDto> searchItem(String text, long userId);

    CommentDto addComment(Long authorId, Long itemId, CommentOutDto commentOutDto);
}