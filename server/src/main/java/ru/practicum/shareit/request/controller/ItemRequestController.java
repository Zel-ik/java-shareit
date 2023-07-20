package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@RequestHeader(USER_HEADER) Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getRequestsByUser(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
