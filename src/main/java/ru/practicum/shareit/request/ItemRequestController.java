package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(value = USER_ID) Long userId, @Valid @RequestBody ItemRequestDtoIn itemRequestDto) {
        log.info("Получен запрос 'Post /requests'");
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestDtoOut> getAllOwnRequest(@RequestHeader(value = USER_ID) Long userId) {
        log.info("Получен запрос 'Get /requests'");
        return itemRequestService.getAllOwnRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllRequests(@RequestHeader(value = USER_ID) Long userId,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        log.info("Получен запрос 'Get /requests/all'");
        return itemRequestService.getAllRequests(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getRequestById(@RequestHeader(value = USER_ID) Long userId, @PathVariable Long requestId) {
        log.info("Получен запрос 'Get /requests/{requestId}'");
        return itemRequestService.getRequestById(userId, requestId);
    }
}