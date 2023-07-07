package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private void setItems(ItemRequestDto requestDto) {
        List<ItemDto> items = itemRepository.findAllByRequestId(requestDto.getId()).stream()
                .map(ItemMapper.INSTANCE::itemToItemDto)
                .collect(Collectors.toList());
        requestDto.setItems(items);
    }

    @Transactional
    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("createRequest: {}, userId={}", itemRequestDto, userId);
        ItemRequest request = RequestMapper.INSTANCE.requestDtoToRequest(itemRequestDto);
        User requester = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return RequestMapper.INSTANCE.requestToRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getRequestsByUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        List<ItemRequestDto> itemRequestsDto = RequestMapper.INSTANCE
                .requestsToRequestsDto(requestRepository.findByRequesterIdOrderByCreatedDesc(userId));
        itemRequestsDto.forEach(this::setItems);
        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = new CustomPageRequest(from, size);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        List<ItemRequestDto> itemRequestsDto = RequestMapper.INSTANCE
                .requestsToRequestsDto(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageRequest)
                        .getContent());
        itemRequestsDto.forEach(this::setItems);
        return itemRequestsDto;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        ItemRequestDto requestDto = RequestMapper.INSTANCE.requestToRequestDto(requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("request with id={} not exist", requestId);
                    throw new EntityNotExistException(String.format("Запрос с id=%d не существует.", requestId));
                }));
        setItems(requestDto);
        return requestDto;
    }
}
