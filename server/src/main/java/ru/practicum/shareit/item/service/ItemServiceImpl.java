package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.mapper.CommentMapper;
import ru.practicum.shareit.item.model.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        log.info("Create item");
        Item item = itemRepository.save(itemMapper.mapFrom(itemDto, userId));
        return itemMapper.mapTo(item, userId);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        log.info("Update item with id {}", itemId);
        Item itemBefore = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId)));

        if (itemBefore.getUser().getId() != userId) {
            throw new EntityNotFoundException(String.format("Вещь %s не принадлежит пользователю с идентификатором %d",
                    itemBefore.getName(), userId));
        }

        Item item = itemMapper.mapFrom(itemDto, itemBefore);
        return itemMapper.mapTo(item, userId);
    }

    @Override
    public ItemDto getItem(long itemId, long userId) {
        log.info("Get item with id {}", itemId);
        return itemMapper.mapTo(itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Text")), userId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        log.info("Get users items with userId {}", userId);
        List<Item> items = itemRepository.findAllByUserId(userId);
        return items.stream()
                .map(item -> itemMapper.mapTo(item, userId))
                .sorted(Comparator.comparing((ItemDto item) ->
                        (item.getLastBooking() != null && item.getNextBooking() != null) ? 0 : 1))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text, long userId) {
        log.info("Get items by text {}", text);
        return text.isBlank() ? new ArrayList<>() : itemRepository.itemsByText(text.toUpperCase()).stream()
                .map(item -> itemMapper.mapTo(item, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, long userId, long itemId) {
        log.info("Create comment {}", commentDto.getText());
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId)));
        Comment comment = commentRepository.save(commentMapper.mapFrom(commentDto, item, userId));
        return commentMapper.mapTo(comment);
    }
}
