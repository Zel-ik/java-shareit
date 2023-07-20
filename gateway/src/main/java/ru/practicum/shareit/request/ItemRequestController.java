package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.ItemController.USER_HEADER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_HEADER) Long userId,
                                                @Validated @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return requestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Get requests by user with userId={}", userId);
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}
