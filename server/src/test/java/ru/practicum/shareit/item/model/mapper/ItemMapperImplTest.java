package ru.practicum.shareit.item.model.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemMapperImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemMapperImpl itemMapper;

    private ItemDto itemDto;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("User");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Item request comment");
    }

    @Test
    public void testMapFrom_withUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Item item = itemMapper.mapFrom(itemDto, 1L);

        Assertions.assertNotNull(item);
        Assertions.assertEquals(itemDto.getId(), item.getId());
        Assertions.assertEquals(itemDto.getName(), item.getName());
        Assertions.assertEquals(itemDto.getDescription(), item.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), item.getAvailable());
        Assertions.assertNotNull(item.getUser());
        Assertions.assertEquals(user.getEmail(), item.getUser().getEmail());
        Assertions.assertEquals(user.getName(), item.getUser().getName());
    }

    @Test
    public void testMapFrom_withItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Old Name");
        item.setDescription("Old description");
        item.setAvailable(false);

        itemDto.setName("New Name");
        itemDto.setDescription("New description");
        itemDto.setAvailable(true);

        Item result = itemMapper.mapFrom(itemDto, item);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemDto.getName(), result.getName());
        Assertions.assertEquals(itemDto.getDescription(), result.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), result.getAvailable());
    }

    @Test
    public void testMapFrom_withEmptyItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Old Name");
        item.setDescription("Old description");
        item.setAvailable(false);

        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);

        Assertions.assertThrows(ConflictException.class, () -> itemMapper.mapFrom(itemDto, item));
    }

    @Test
    public void testMapFrom_withWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemMapper.mapFrom(itemDto, 1L));
    }

    @Test
    public void testMapFrom_withWrongRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        itemDto.setRequestId(2L);

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemMapper.mapFrom(itemDto, 1L));
    }
}