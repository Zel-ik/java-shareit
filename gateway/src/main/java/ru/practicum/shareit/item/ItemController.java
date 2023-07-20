package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validationgroup.CreateGroup;
import ru.practicum.shareit.validationgroup.UpdateGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_HEADER) Long userId,
                                             @Validated(CreateGroup.class) @RequestBody ItemDto itemDto) {
        log.info("Creating item {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @Validated(UpdateGroup.class) @RequestBody ItemDto itemDto) {
        log.info("Updating item {}, itemId={}", itemDto, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_HEADER) Long userId,
                           @PathVariable Long itemId) {
        log.info("Deleting item with itemId={}", itemId);
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Getting item with itemId={}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(USER_HEADER) Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get items of user with userId={}", userId);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_HEADER) Long userId,
                                             @RequestParam String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Searching items with text={}", text);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment {}, itemId={}", commentDto, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }


}
