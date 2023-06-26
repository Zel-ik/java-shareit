package ru.practicum.shareit.request.model.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(UserMapper.toUserDto(itemRequest.getRequester()))
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDtoIn itemRequestDtoIn, User user, LocalDateTime time) {
        return ItemRequest.builder()
                .id(itemRequestDtoIn.getId())
                .description(itemRequestDtoIn.getDescription())
                .requester(user)
                .created(time)
                .build();
    }

    public static ItemRequestDtoOut toItemRequestDtoOut(ItemRequest itemRequest, List<ItemDto> items) {
        return ItemRequestDtoOut.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(UserMapper.toUserDto(itemRequest.getRequester()))
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}