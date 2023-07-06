package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentOutDto;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(value = USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос 'POST /items'");
        ;
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = USER_ID) Long userId, @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        log.info(String.format("Получен запрос 'PATCH /items/%d'", itemId));
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItemById(@RequestHeader(value = USER_ID) Long userId, @PathVariable Long itemId) {
        log.info(String.format("Получен запрос 'GET /items/%d'", itemId));
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemBookingDto> getAllItem(@RequestHeader(value = USER_ID) Long userId) {
        log.info(String.format("Получен запрос 'GET /items' от пользователя %d'", userId));
        return itemService.getAllItem(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestHeader(value = USER_ID) Long userId, @RequestParam(name = "text") String text) {
        log.info("Получен запрос 'GET /items/search/?text='" + text);
        return itemService.searchItem(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(value = USER_ID) Long userId, @PathVariable Long itemId, @RequestBody CommentOutDto comment) {
        return itemService.addComment(userId, itemId, comment);
    }
}