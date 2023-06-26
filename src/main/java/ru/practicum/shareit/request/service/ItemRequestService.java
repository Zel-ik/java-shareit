package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoOut;

import java.util.Collection;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(ItemRequestDtoIn itemRequestDtoIn, Long userId);

    Collection<ItemRequestDtoOut> getAllOwnRequest(Long userId);

    List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoOut getRequestById(Long userId, Long requestId);
}