package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constans.State;
import ru.practicum.shareit.constans.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private Booking booking;
    private BookingDtoIn bookingDtoIn;
    private BookingDtoIn bookingRejDtoIn;
    private Booking bookingRej;
    private User user;
    private User otherUser;
    private Item item;
    private Item item2;
    private LocalDateTime time;

    @BeforeEach
    void creteModel() {
        user = User.builder()
                .id(1L)
                .email("oleg@yandex.com")
                .name("oleg")
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("artem@yandex.com")
                .name("Artem")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description item")
                .available(true)
                .owner(user)
                .requestId(1L)
                .build();

        item2 = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description item")
                .available(false)
                .owner(user)
                .requestId(1L)
                .build();

        bookingDtoIn = BookingDtoIn.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .bookerId(otherUser.getId())
                .status("WAITING")
                .build();
        booking = BookingMapper.toBooking(bookingDtoIn, otherUser, item);
        bookingRejDtoIn = BookingDtoIn.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item2.getId())
                .bookerId(otherUser.getId())
                .status("REJECTED")
                .build();
        bookingRej = BookingMapper.toBooking(bookingRejDtoIn, otherUser, item);
        time = LocalDateTime.now();
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto bookingTest = bookingService.saveBooking(bookingDtoIn,
                otherUser.getId());
        assertEquals(1, bookingTest.getId());
        assertEquals(bookingDtoIn.getStart(), bookingTest.getStart());
        assertEquals(bookingDtoIn.getEnd(), bookingTest.getEnd());
        assertEquals(otherUser.getId(), bookingTest.getBooker().getId());
    }

    @Test
    void createBookingTestNotAvalible() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));
        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.saveBooking(bookingRejDtoIn,
                otherUser.getId()));
        assertEquals("Вещь не доступна к бронированию", exception.getMessage());
    }

    @Test
    void createBookingTestWithOwnerUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.saveBooking(bookingDtoIn,
                user.getId()));
        assertEquals("Вещь не может быть забронирована, так как пользователь хояин вещи", exception.getMessage());
    }

    @Test
    void confirmBookingTest() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));

        bookingService.confirmBooking(true, booking.getId(),
                user.getId());
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    void confirmBookingNotOwnerTest() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.confirmBooking(true, booking.getId(), otherUser.getId()));
        assertEquals("Данный пользователь не хозяин вещи", exception.getMessage());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        BookingDto bookingTest = bookingService.getBookingById(booking.getId(),
                user.getId());
        assertEquals(1, bookingTest.getId());
        assertEquals(bookingDtoIn.getStart(), bookingTest.getStart());
        assertEquals(bookingDtoIn.getEnd(), bookingTest.getEnd());
        assertEquals(otherUser.getId(), bookingTest.getBooker().getId());
    }

    @Test
    void getBookingByIdTestNotOwner() {
        User wrongUser = new User(3,"user","122@sa.ru");
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(booking.getId(),
                        wrongUser.getId()));
        assertEquals("Указанный пользователь не сущетсвует", exception.getMessage());
    }

    @Test
    void getAllBookingWithStateAll() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(otherUser.getId())).thenReturn(List.of(booking));

        Collection<BookingDto> bookingTest = bookingService.getAllBooking(
                otherUser.getId(), State.ALL, null, null);
        assertEquals(1, bookingTest.size());
    }

    @Test
    void getAllBookingWithStateUNSUPPORTED() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        UnsupportedException exception = assertThrows(UnsupportedException.class, () ->
                bookingService.getAllBooking(
                        otherUser.getId(), State.UNSUPPORTED_STATUS, null, null));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getAllBookingWithStateWaiting() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class))).thenReturn(List.of(booking));

        Collection<BookingDto> bookingTest = bookingService.getAllBooking(
                otherUser.getId(), State.WAITING, null, null);
        assertEquals(1, bookingTest.size());
    }

    @Test
    void getAllBookingWithStateCurrent() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of(booking));

        Collection<BookingDto> bookingTest = bookingService.getAllBooking(
                otherUser.getId(), State.CURRENT, null, null);
        assertEquals(1, bookingTest.size());
    }

    @Test
    void getAllBookingWithStateRej() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(Status.class))).thenReturn(List.of(bookingRej));
        bookingRej.setStatus(Status.REJECTED);
        Collection<BookingDto> bookingTest = bookingService.getAllBooking(
                otherUser.getId(), State.REJECTED, null, null);
        assertEquals(1, bookingTest.size());
        assertEquals(Status.REJECTED, bookingTest.stream().collect(Collectors.toList()).get(0).getStatus());
    }

    @Test
    void getAllBookingForItemsWithoutPag() {
        List<Booking> allBooking = List.of(
                Booking.builder()
                        .id(2L)
                        .start(LocalDateTime.now().plusDays(10))
                        .end(LocalDateTime.now().plusDays(20))
                        .item(item)
                        .booker(otherUser)
                        .status(Status.APPROVED)
                        .build(),
                Booking.builder()
                        .id(3L)
                        .start(LocalDateTime.now().plusDays(5))
                        .end(LocalDateTime.now().plusDays(6))
                        .item(item)
                        .booker(otherUser)
                        .status(Status.REJECTED)
                        .build());
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(otherUser.getId()))
                .thenReturn(allBooking);

        Collection<BookingDto> bookingTest3 = bookingService.getAllBookingForItems(
                otherUser.getId(), State.ALL, null, null);
        assertEquals(2, bookingTest3.size());
    }
}