package ru.practicum.shareit.request.model.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemRequestMapperImpl implements ItemRequestMapper {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto mapTo(ItemRequest i) {
        return new ItemRequestDto(i.getId(), i.getDescription(), i.getCreated(), getItems(i));
    }

    @Override
    public ItemRequest mapFrom(ItemRequestDto entity) {
        return null;
    }

    @Override
    public ItemRequest mapFrom(ItemRequestDto i, Long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User" +
                " %s not found", userId)));
        return new ItemRequest(i.getId(), i.getDescription(), LocalDateTime.now(), requester, new ArrayList<>());
    }

    private List<ItemDtoForRequest> getItems(ItemRequest i) {
        return itemRepository.findByItemRequestIdAndAvailableTrue(i.getId()).stream()
                .map(item -> new ItemDtoForRequest(item.getId(), item.getName(), item.getDescription(),
                        item.getAvailable(), item.getItemRequest().getId()))
                .collect(Collectors.toList());
    }
}
