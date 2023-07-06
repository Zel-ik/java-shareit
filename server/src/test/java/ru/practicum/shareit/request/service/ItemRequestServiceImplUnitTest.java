package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private long requesterId;
    private User requester;
    private ItemRequestDto itemRequestDtoFrom;
    private ItemRequestDto itemRequestDtoTo;
    private ItemRequest itemRequestDB;
    private List<ItemRequest> itemRequests;

    @BeforeEach
    void setUp() {
        requesterId = 1L;
        requester = new User(requesterId, "Email@mail.ru", "Name");
        itemRequestDtoFrom = new ItemRequestDto(null, "Description", LocalDateTime.now(), null);
        itemRequestDB = new ItemRequest(1L, itemRequestDtoFrom.getDescription(), itemRequestDtoFrom.getCreated(),
                requester, null);
        itemRequestDtoTo = new ItemRequestDto(1L, "Description", itemRequestDtoFrom.getCreated(),
                null);
        itemRequests = new ArrayList<>();
        itemRequests.add(itemRequestDB);
    }

    @Test
    void createItemRequestTest() {
        when(itemRequestMapper.mapFrom(itemRequestDtoFrom, 1L)).thenReturn(itemRequestDB);
        when(itemRequestRepository.save(itemRequestDB)).thenReturn(itemRequestDB);
        when(itemRequestMapper.mapTo(itemRequestDB)).thenReturn(itemRequestDtoTo);

        itemRequestService.createItemRequest(itemRequestDtoFrom, requesterId);

        verify(itemRequestMapper, times(1)).mapFrom(eq(itemRequestDtoFrom), anyLong());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
        verify(itemRequestMapper, times(1)).mapTo(any(ItemRequest.class));
    }

    @Test
    void getItemRequestsTest() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(requester));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(itemRequests);
        when(itemRequestMapper.mapTo(any(ItemRequest.class))).thenReturn(itemRequestDtoTo);

        List<ItemRequestDto> result = itemRequestService.getItemRequests(requester.getId());

        assertEquals(result.size(), itemRequests.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(anyLong());
        verify(itemRequestMapper, times(itemRequests.size())).mapTo(any(ItemRequest.class));
    }

    @Test
    void getItemWithPaginationTest() {
        int from = 0;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size);

        when(itemRequestRepository.findAllByRequesterIdNot(eq(requester.getId()), eq(page)))
                .thenReturn(new PageImpl<>(itemRequests));
        when(itemRequestMapper.mapTo(any(ItemRequest.class))).thenReturn(itemRequestDtoTo);

        List<ItemRequestDto> result = itemRequestService.getItemWithPagination(requester.getId(), from, size);

        assertEquals(result.size(), itemRequests.size());

        verify(itemRequestRepository, times(1)).findAllByRequesterIdNot(eq(requester.getId()),
                eq(page));
        verify(itemRequestMapper, times(itemRequests.size())).mapTo(any(ItemRequest.class));
    }

    @Test
    void getItemRequestTest() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(requester));
        when(itemRequestRepository.findById(eq(itemRequestDtoTo.getId())))
                .thenReturn(java.util.Optional.ofNullable(itemRequests.get(0)));
        when(itemRequestMapper.mapTo(any(ItemRequest.class))).thenReturn(itemRequestDtoTo);

        ItemRequestDto result = itemRequestService.getItemRequest(itemRequestDtoTo.getId(), requester.getId());

        assertEquals(result.getId(), itemRequestDtoTo.getId());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(eq(itemRequestDtoTo.getId()));
        verify(itemRequestMapper, times(1)).mapTo(any(ItemRequest.class));
    }

    @Test
    public void getItemRequests_shouldThrowException_whenUserNotFound() {
        long userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getItemRequests(userId));
    }

    @Test
    public void getItemRequest_shouldThrowException_whenUserNotFound() {
        long userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getItemRequest(1L, userId));
    }

    @Test
    public void getItemWithPagination_shouldReturnEmptyList_whenNoItemsFound() {
        long userId = 1;
        int from = 0;
        int size = 10;

        Page<ItemRequest> page = new PageImpl<>(new ArrayList<>());
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any(Pageable.class))).thenReturn(page);

        List<ItemRequestDto> result = itemRequestService.getItemWithPagination(userId, from, size);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetItemRequestThrowsExceptionWhenNotFound() {
        long itemRequestId = 1L;
        long userId = 2L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(itemRequestId, userId),
                String.format("Item request %s not found", itemRequestId));
    }
}