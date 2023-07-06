package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoFrom;
import ru.practicum.shareit.booking.model.dto.BookingDtoTo;
import ru.practicum.shareit.booking.model.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDtoForBooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplUnitTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

 @Test
    void createBooking_throwsEntityNotFoundException_whenBookerAndItemOwnersAreSame() {
        User user = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, user, "Name", "Description", true, null);
        BookingDtoFrom bookingDtoFrom = new BookingDtoFrom(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                null, user.getId(), item.getId());
        Booking bookingBefore = new Booking(1L, bookingDtoFrom.getStart(), bookingDtoFrom.getEnd(),
                BookingStatus.WAITING, user, item);
        when(bookingMapper.mapFrom(bookingDtoFrom)).thenReturn(bookingBefore);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(bookingDtoFrom, user.getId()));
    }

    @Test
    void createBooking_throwsBadRequestException_whenItemIsNotAvailable() {
        User user = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, user, "Name", "Description", false, null);
        BookingDtoFrom bookingDtoFrom = new BookingDtoFrom(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                null, user.getId(), item.getId());
        Booking bookingBefore = new Booking(1L, bookingDtoFrom.getStart(), bookingDtoFrom.getEnd(),
                BookingStatus.WAITING, new User(2L, "test2@test.com", "Test2"), item);
        when(bookingMapper.mapFrom(bookingDtoFrom)).thenReturn(bookingBefore);

        assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(bookingDtoFrom, user.getId()));
    }

    @Test
    void createBooking_returnsBookingDtoTo_whenBookingCreated() {
        User user = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, user, "Name", "Description", true, null);

        BookingDtoFrom bookingDtoFrom = new BookingDtoFrom(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                null, user.getId(), item.getId());

        Booking bookingBefore = new Booking(1L, bookingDtoFrom.getStart(), bookingDtoFrom.getEnd(),
                BookingStatus.WAITING, new User(2L, "test2@test.com", "Test2"), item);

        Booking bookingAfter = new Booking(1L, bookingDtoFrom.getStart(), bookingDtoFrom.getEnd(),
                BookingStatus.WAITING, user, item);

        BookingDtoTo bookingDtoToExpected = new BookingDtoTo(bookingAfter.getId(), bookingDtoFrom.getStart(),
                bookingDtoFrom.getEnd(), bookingAfter.getStatus(),
                new UserDtoForBooking(user.getId()),
                new ItemDtoForBooking(item.getId(), item.getName()));

        when(bookingMapper.mapFrom(bookingDtoFrom)).thenReturn(bookingBefore);
        when(bookingRepository.save(bookingBefore)).thenReturn(bookingAfter);
        when(bookingMapper.mapToWithClasses(bookingAfter)).thenReturn(bookingDtoToExpected);

        BookingDtoTo bookingDtoToActual = bookingService.createBooking(bookingDtoFrom, user.getId());

        assertThat(bookingDtoToActual).isEqualTo(bookingDtoToExpected);
    }

    @Test
    void testMakeApprovedBooking_BookingNotFound() {
        long userId = 1L;
        long bookingId = 2L;
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.makeApprovedBooking(userId, bookingId, approved));

        assertEquals("Бронь с id " + bookingId + " не найдена", exception.getMessage());
    }

    @Test
    void testMakeApprovedBooking_UserNotOwner() {
        long userId = 1L;
        long bookingId = 2L;
        boolean approved = true;

        User user = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, new User(2L, "test2@test.com", "Test2"), "Name",
                "Description", true, null);
        Booking booking = new Booking(2L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING, user, item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.makeApprovedBooking(userId, bookingId, approved));

        assertEquals("Вы не можете подтвердить бронь, так как не являетесь владельцем вещи: Name",
                exception.getMessage());
    }

    @Test
    void testMakeApprovedBooking_StatusAlreadySet() {
        long userId = 1L;
        long bookingId = 2L;
        boolean approved = true;

        User user = new User(1L, "test1@test.com", "Test1");
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.APPROVED, user,
                new Item(1L, user, "Name", "Description", true, null));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.makeApprovedBooking(userId, bookingId, approved));

        assertEquals("Статус уже approved", exception.getMessage());
    }

    @Test
    void testMakeApprovedBooking_Success() {
        long userId = 1L;
        long bookingId = 2L;
        boolean approved = true;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING,
                booker, item);
        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToWithClasses(booking)).thenReturn(bookingDtoTo);

        bookingService.makeApprovedBooking(userId, bookingId, approved);

        assertEquals(BookingStatus.APPROVED, bookingDtoTo.getStatus());
        verify(bookingRepository).findById(bookingId);
        verify(bookingMapper, times(1)).mapToWithClasses(booking);
    }

    @Test
    public void testGetBookingWhenUserHasAccess() {
        long userId = 1L;
        long bookingId = 2L;
        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING,
                booker, item);
        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToWithClasses(booking)).thenReturn(bookingDtoTo);

        BookingDtoTo actualBookingDto = bookingService.getBooking(userId, bookingId);

        assertEquals(bookingDtoTo, actualBookingDto);
    }

    @Test
    public void testGetBookingWhenUserDoesNotHaveAccess() {
        long userId = 3L;
        long bookingId = 2L;
        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING,
                booker, item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));
        assertEquals("У вас нет доступа к этой брони", exception.getMessage());
    }

    @Test
    public void testGetBookingWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(1L, 2L));
        assertEquals("Бронь с id 2 не найдена", exception.getMessage());
    }

    @Test
    void testGetUserBookings_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING,
                booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findBookingsByBookerId(userId, PageRequest.of(from / size, size)))
                .thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getUserBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetUserBookingsPast_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "PAST";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5),
                BookingStatus.WAITING, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findPastBookingsByBookerId(eq(userId), any(LocalDateTime.class),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getUserBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetUserBookingsCurrent_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "CURRENT";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(5),
                BookingStatus.WAITING, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findCurrentBookingsByBookerId(eq(userId), any(LocalDateTime.class),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getUserBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetUserBookingsFuture_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "FUTURE";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(15),
                BookingStatus.WAITING, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findFutureBookingsByBookerId(eq(userId), any(LocalDateTime.class),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getUserBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetUserBookingsStatusWaiting_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "WAITING";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(15),
                BookingStatus.WAITING, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.WAITING, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findWaitingBookingsByBookerId(eq(userId), eq(BookingStatus.WAITING),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getUserBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetUserBookingsStatusRejected_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "REJECTED";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(15),
                BookingStatus.REJECTED, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.REJECTED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findRejectedBookingsByBookerId(eq(userId), eq(BookingStatus.REJECTED),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getUserBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetUserBookings_withUnknownState_shouldThrowBadRequestException() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        assertThrows(BadRequestException.class,
                () -> bookingService.getUserBookings(userId, "UNSUPPORTED_STATUS", from, size));
    }

    @Test
    void testGetUserBookings_withEmptyBookingList_shouldThrowEntityNotFoundException() {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;

        when(bookingRepository.findBookingsByBookerId(userId, PageRequest.of(from / size, size)))
                .thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getUserBookings(userId, state, from, size));
    }

    @Test
    void testGetOwnerBookings_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING,
                booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findByItemOwnerIdOrderByStartDateDesc(userId, PageRequest.of(from / size, size)))
                .thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetOwnerBookingsPast_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "PAST";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5),
                BookingStatus.WAITING, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findPastBookingsByItemOwnerIdOrderByStartDateDesc(eq(userId), any(LocalDateTime.class),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetOwnerBookingsCurrent_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "CURRENT";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(5),
                BookingStatus.WAITING, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findCurrentBookingsByItemOwnerIdOrderByStartDateDesc(eq(userId), any(LocalDateTime.class),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetOwnerBookingsFuture_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "FUTURE";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(15),
                BookingStatus.WAITING, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findFutureBookingsByItemOwnerIdOrderByStartDateDesc(eq(userId), any(LocalDateTime.class),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetOwnerBookingsStatusWaiting_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "WAITING";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(15),
                BookingStatus.WAITING, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.WAITING, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDateDesc(eq(userId), eq(BookingStatus.WAITING),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetOwnerBookingsStatusRejected_withValidInputParameters_shouldReturnListOfBookingDto() {
        long userId = 1L;
        String state = "REJECTED";
        int from = 0;
        int size = 10;

        User owner = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, owner, "Name", "Description", true, new ArrayList<>());
        User booker = new User(2L, "test2@test.com", "Test2");

        Booking booking = new Booking(2L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(15),
                BookingStatus.REJECTED, booker, item);

        BookingDtoTo bookingDtoTo = new BookingDtoTo(2L, booking.getStartDate(), booking.getEndDate(),
                BookingStatus.REJECTED, new UserDtoForBooking(booker.getId()), new ItemDtoForBooking(item.getId(),
                item.getName()));

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDateDesc(eq(userId), eq(BookingStatus.REJECTED),
                eq(PageRequest.of(from / size, size)))).thenReturn(List.of(booking));

        when(bookingMapper.mapToWithClasses(any(Booking.class))).thenReturn(bookingDtoTo);

        List<BookingDtoTo> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDtoTo, result.get(0));
    }

    @Test
    void testGetOwnerBookings_withUnknownState_shouldThrowBadRequestException() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        assertThrows(BadRequestException.class,
                () -> bookingService.getOwnerBookings(userId, "UNSUPPORTED_STATUS", from, size));
    }

    @Test
    void testGetOwnerBookings_withEmptyBookingList_shouldThrowEntityNotFoundException() {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;

        when(bookingRepository.findByItemOwnerIdOrderByStartDateDesc(userId, PageRequest.of(from / size, size)))
                .thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getOwnerBookings(userId, state, from, size));
    }
}