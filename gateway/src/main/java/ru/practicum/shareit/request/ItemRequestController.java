package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validateInterfaces.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.ItemController.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestBody @Validated(Create.class) ItemRequestDto itemRequest,
                                                    @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Request create with request {}, userId = {}", itemRequest, userId);
        return itemRequestClient.createItemRequest(itemRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Request get owner with userId = {}", userId);
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequests(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Request get otherRequest with userId = {}, from = {}, size = {}", userId, from, size);
        return itemRequestClient.getItemWithPagination(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @PathVariable long requestId) {
        log.info("Request get with requestId = {}, userId = {}", requestId, userId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}

