package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequest,
                                            @RequestHeader(X_SHARER_USER_ID) long userId) {
        return itemRequestService.createItemRequest(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnerRequests(@RequestHeader(X_SHARER_USER_ID) long userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherRequests(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemRequestService.getItemWithPagination(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                     @PathVariable long requestId) {
        return itemRequestService.getItemRequest(requestId, userId);
    }
}
