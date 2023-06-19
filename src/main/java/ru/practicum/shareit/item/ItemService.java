package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(long itemId, ItemDto itemDto, long ownerId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getUserItems(long userId);

    List<ItemDto> searchItem(String searchRequest, long userId);

    void deleteItemById(long itemId, long userId);

    Item findItemById(long itemId);

    CommentDto createComment(CommentDto comment, long itemId, long commentatorId);
}