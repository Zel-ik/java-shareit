package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getItemRequests(long userId);

    List<ItemRequestDto> getItemWithPagination(long userId, int from, int size);

    ItemRequestDto getItemRequest(long itemRequestId, long userId);
}
