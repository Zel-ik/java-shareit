package ru.practicum.shareit.item.model.mapper;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;

import java.util.List;

public interface CommentMapper {
    Comment mapFrom(CommentDto entity, Item item, long userId);

    CommentDto mapTo(Comment entity);

    List<CommentDto> getComments(Long itemId);
}
