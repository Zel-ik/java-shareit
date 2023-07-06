package ru.practicum.shareit.booking.model.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoFrom;
import ru.practicum.shareit.booking.model.dto.BookingDtoTo;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDtoForBooking;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingMapperImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingMapperImpl bookingMapper;

    @Test
    public void testMapToWithClasses() {
        User user = new User(1L, "test@test.com", "test name");
        Item item = new Item(2L, user, "test item name", "test item description", true, null);
        Booking booking = new Booking(3L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, user, item);

        BookingDtoTo dto = bookingMapper.mapToWithClasses(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStatus(), dto.getStatus());

        UserDtoForBooking bookerDto = dto.getBooker();
        assertNotNull(bookerDto);
        assertEquals(user.getId(), bookerDto.getId());

        ItemDtoForBooking itemDto = dto.getItem();
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
    }

    @Test
    public void testMapToWithClasses_NullBooking() {
        assertThrows(NullPointerException.class, () -> bookingMapper.mapToWithClasses(null));
    }

    @Test
    public void testMapTo() {
        User user = new User(1L, "test@test.com", "test name");
        Item item = new Item(2L, user, "test item name", "test item description", true, null);
        Booking booking = new Booking(3L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, user, item);

        BookingDtoFrom dto = bookingMapper.mapTo(booking);

        assertNotNull(booking);
        assertEquals(dto.getStatus(), booking.getStatus());
        assertEquals(dto.getBookerId(), booking.getBooker().getId());
        assertEquals(dto.getItemId(), booking.getItem().getId());
    }

    @Test
    public void testMapTo_NullDto() {
        assertThrows(NullPointerException.class, () -> bookingMapper.mapTo(null));
    }

    @Test
    void mapFrom_shouldMapDtoToEntity() {
        User user = new User(1L, "test@test.com", "Test");
        Item item = new Item(1L, user, "Item 1", "Description", true, null);
        BookingDtoFrom dto = new BookingDtoFrom(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, 1L, 1L);

        when(userRepository.findById(dto.getBookerId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.of(item));
        Booking actualBooking = bookingMapper.mapFrom(dto);

        assertEquals(dto.getStart(), actualBooking.getStartDate());
        assertEquals(dto.getEnd(), actualBooking.getEndDate());
        assertEquals(dto.getStatus(), actualBooking.getStatus());
        assertEquals(user, actualBooking.getBooker());
        assertEquals(item, actualBooking.getItem());
    }

    @Test
    void mapFrom_shouldThrowExceptionWhenUserNotFound() {
        BookingDtoFrom dto = new BookingDtoFrom(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, 1L, 1L);

        when(userRepository.findById(dto.getBookerId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingMapper.mapFrom(dto));
    }

    @Test
    void mapFrom_shouldThrowExceptionWhenItemNotFound() {
        User user = new User(1L, "test@test.com", "Test");
        BookingDtoFrom dto = new BookingDtoFrom(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, 1L, 1L);

        when(userRepository.findById(dto.getBookerId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingMapper.mapFrom(dto));
    }
}