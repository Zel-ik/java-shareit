package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.dto.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    private User user;
    private User otherUser;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoOut itemRequestDtoOut;
    private ItemRequestDtoIn itemRequestDtoIn;
    private LocalDateTime time = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("oleg@yandex.com")
                .name("oleg")
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("a@yandex.com")
                .name("artem")
                .build();

        itemRequestDtoIn = ItemRequestDtoIn.builder()
                .id(1L)
                .description("Пила")
                .userId(user.getId())
                .created(time)
                .build();

        List<ItemDto> items = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Item")
                        .description("Description item")
                        .available(true)
                        .requestId(1L)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("Item2")
                        .description("Description item2")
                        .available(true)
                        .requestId(1L)
                        .build()
        );
        itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoIn, user, time);
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(itemRequest, items);
    }

    @Test
    void addItemRequestGood() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDto request = itemRequestService.addItemRequest(itemRequestDtoIn, user.getId());
        assertEquals(request.getId(), itemRequest.getId());
    }

    @Test
    void addItemRequestUserWrong() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.addItemRequest(itemRequestDtoIn, 11L));
        assertEquals("Указанный пользователь не сущетсвует", exception.getMessage());
    }

    @Test
    void getAllOwnRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<ItemRequest> requests = List.of(
                itemRequest,
                new ItemRequest(2L, "Ручка", user, time)
        );
        when(itemRequestRepository.findAllByRequester_Id(anyLong())).thenReturn(requests);
        List result = (List) itemRequestService.getAllOwnRequest(user.getId());
        assertEquals(2, result.size());
    }

    @Test
    void getAllRequestsWithPag() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(otherUser));
        List<ItemRequest> requests = List.of(
                itemRequest,
                new ItemRequest(2L, "Ручка", user, time),
                new ItemRequest(3L, "Карандаш", user, time),
                new ItemRequest(4L, "Стол", user, time),
                new ItemRequest(5L, "Ручка гелевая", user, time)
        );
        when(itemRequestRepository.findAllByRequester_IdNot(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(requests));
        List result = itemRequestService.getAllRequests(otherUser.getId(), 0, 10);
        assertEquals(5, result.size());
    }

    @Test
    void getAllOwnRequestWrongUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(otherUser));
        List result = (List) itemRequestService.getAllOwnRequest(otherUser.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(otherUser));
        List<ItemRequest> requests = List.of(
                itemRequest,
                new ItemRequest(2L, "Ручка", user, time)
        );
        when(itemRequestRepository.findAllByRequester_IdNot(anyLong())).thenReturn(requests);
        List result = itemRequestService.getAllRequests(otherUser.getId(), null, null);
        assertEquals(2, result.size());
    }

    @Test
    void getAllRequestsWrongUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(otherUser));
        List result = itemRequestService.getAllRequests(otherUser.getId(), null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(itemRequest));
        ItemRequestDtoOut request = itemRequestService.getRequestById(user.getId(),itemRequest.getId());
        assertEquals(request.getId(), itemRequest.getId());
    }

    @Test
    void getRequestByWrongId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(user.getId(),55L));
        assertEquals("Не существует запроса с данным id", exception.getMessage());
    }
}