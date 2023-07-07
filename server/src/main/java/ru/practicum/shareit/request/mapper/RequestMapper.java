package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    ItemRequest requestDtoToRequest(ItemRequestDto requestDto);

    ItemRequestDto requestToRequestDto(ItemRequest itemRequest);

    List<ItemRequestDto> requestsToRequestsDto(List<ItemRequest> requests);
}
