package ru.practicum.shareit.request.model.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestMapperImpl itemRequestMapper;

    private ItemRequestDto itemRequestDto;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Some description");
        itemRequestDto.setCreated(null);
        itemRequestDto.setItems(new ArrayList<>());
    }

    @Test
    void testMapFrom() {
        // Arrange
        User user = new User(userId, "test@example.com", "John Doe");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ItemRequest itemRequest = itemRequestMapper.mapFrom(itemRequestDto, userId);

        // Assert
        assertNotNull(itemRequest);
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertNotNull(itemRequest.getCreated());
        assertEquals(user, itemRequest.getRequester());
        assertTrue(itemRequest.getItems().isEmpty());
    }

    @Test
    void testMapFromThrowsExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(EntityNotFoundException.class, () -> itemRequestMapper.mapFrom(itemRequestDto, userId));
    }

}