package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId) {
        return itemRequestMapper.mapTo(itemRequestRepository.save(itemRequestMapper.mapFrom(itemRequestDto, userId)));
    }

    @Override
    public List<ItemRequestDto> getItemRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User %s not found", userId)));
        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(itemRequestMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getItemWithPagination(long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        return itemRequestRepository.findAllByRequesterIdNot(userId, page)
                .map(itemRequestMapper::mapTo)
                .getContent();
    }

    @Override
    public ItemRequestDto getItemRequest(long itemRequestId, long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User %s not found", userId)));
        return itemRequestMapper.mapTo(itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Item request %s not found", itemRequestId))));
    }
}
