package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validateInterfaces.Create;
import ru.practicum.shareit.validateInterfaces.Update;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";


    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Validated(Create.class) CommentDto commentDto,
                                                @RequestHeader(X_SHARER_USER_ID) long userId,
                                                @PathVariable long itemId) {
        log.info("Comment create with comment {}, userId = {}, itemId = {}", commentDto, userId, itemId);
        return itemClient.createComment(commentDto, userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Validated(Create.class) ItemDto itemDto,
                                             @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Item create with item {}, userId = {}", itemDto, userId);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Validated(Update.class) ItemDto itemDto,
                                             @PathVariable long itemId,
                                             @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Item update with item {}, userId = {}, itemId = {}", itemDto, userId, itemId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Item get with userId = {}, itemId = {}", userId, itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Items get with userId = {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestParam String text,
                                                @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Item get with text {}, userId = {}", text, userId);
        return itemClient.getItemsByText(text, userId);
    }
}
