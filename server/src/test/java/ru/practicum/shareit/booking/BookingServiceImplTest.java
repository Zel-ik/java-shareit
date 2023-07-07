package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.OperationNotAllowed;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService requestService;
    private final BookingService bookingService;
    private ItemDto itemDto;
    private UserDto userDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("test");
        userDto.setEmail("test@test.ru");

        itemDto = new ItemBookingDto();
        itemDto.setId(1L);
        itemDto.setName("test");
        itemDto.setDescription("test description");
        itemDto.setAvailable(true);

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(7));
    }

    @Test
    void createBooking() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);

        BookingResponseDto responseBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);
        assertEquals(otherUserDto, responseBooking.getBooker());
    }

    @Test
    void createBookingUserNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.createBooking(100L, bookingDto));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void createBookingItemNotExist() {
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        bookingDto.setItemId(100L);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.createBooking(otherUserDto.getId(), bookingDto));
        assertEquals("Вещь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void createBookingItemNotAvailable() {
        UserDto createdUser = userService.createUser(userDto);
        itemDto.setAvailable(false);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        OperationNotAllowed ex = assertThrows(OperationNotAllowed.class,
                () -> bookingService.createBooking(otherUserDto.getId(), bookingDto));
        assertEquals(String.format("Вещь с id=%d недоступна.", createdItemDto.getId()), ex.getMessage());
    }

    @Test
    void createBookingByOwner() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.createBooking(createdUser.getId(), bookingDto));
        assertEquals("Вещь не может быть забронирована владельцем.", ex.getMessage());
    }

    @Test
    void approveBooking() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);

        BookingResponseDto responseBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);
        BookingResponseDto approvedBooking = bookingService.approveBooking(createdUser.getId(), responseBooking.getId(), true);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void approveBookingNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.approveBooking(createdUser.getId(), 100L, true));
        assertEquals("Бронирование с id=100 не существует.", ex.getMessage());
    }

    @Test
    void approveBookingByNotOwner() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        BookingResponseDto responseBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);

        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.approveBooking(100L, responseBooking.getId(), true));
        assertEquals("Пользователь с id=100 не является владельцем вещи.", ex.getMessage());
    }

    @Test
    void approveBookingNotWaiting() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        BookingResponseDto responseBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);
        bookingService.approveBooking(createdUser.getId(), responseBooking.getId(), true);

        OperationNotAllowed ex = assertThrows(OperationNotAllowed.class,
                () -> bookingService.approveBooking(createdUser.getId(), responseBooking.getId(), true));
        assertEquals("Бронирование не в статусе ожидания подтверждения.", ex.getMessage());
    }

    @Test
    void getBookingById() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);

        BookingResponseDto responseBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);
        BookingResponseDto readBooking = bookingService.getBookingById(createdUser.getId(), responseBooking.getId());
        assertEquals(responseBooking, readBooking);
    }

    @Test
    void getBookingNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.getBookingById(createdUser.getId(), 100L));
        assertEquals("Бронирование с id=100 не существует.", ex.getMessage());
    }

    @Test
    void getBookingByNotOwner() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        BookingResponseDto responseBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);

        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.getBookingById(100L, responseBooking.getId()));
        assertEquals("Пользователь с id=100 не может просматривать это бронирование.", ex.getMessage());
    }

    @Test
    void getBookingsByUser() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        BookingResponseDto responseBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);
        List<BookingResponseDto> bookingsWaiting = bookingService.getBookingsByUser(otherUserDto.getId(),
                State.WAITING.name(), 0, 10);
        assertEquals(responseBooking, bookingsWaiting.get(0), "Вещь в результате поиска по статусу WAITING не совпадает.");
        assertEquals(1, bookingsWaiting.size(), "Размер полученного списка вещей со статусом WAITING не совпадает.");

        BookingResponseDto rejectedBooking = bookingService.approveBooking(createdUser.getId(), responseBooking.getId(),
                false);
        List<BookingResponseDto> bookingsRejected = bookingService.getBookingsByUser(otherUserDto.getId(),
                State.REJECTED.name(), 0, 10);
        assertEquals(rejectedBooking, bookingsRejected.get(0), "Вещь в результате поиска по статусу REJECTED не совпадает.");
        assertEquals(1, bookingsRejected.size(), "Размер полученного списка вещей со статусом REJECTED не совпадает.");

        List<BookingResponseDto> bookingsAll = bookingService.getBookingsByUser(otherUserDto.getId(),
                State.ALL.name(), 0, 10);
        assertEquals(rejectedBooking, bookingsAll.get(0), "Вещь в результате поиска по всем статусам не совпадает.");
        assertEquals(1, bookingsAll.size(), "Размер полученного списка вещей со всеми статусами не совпадает.");
    }

    @Test
    void getBookingsPastFutureCurrent() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        bookingDto.setStart(LocalDateTime.now().minusDays(10));
        bookingDto.setEnd(LocalDateTime.now().minusDays(5));
        BookingResponseDto responsePastBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);

        BookingDto futureBookingDto = new BookingDto();
        futureBookingDto.setStart(LocalDateTime.now().plusDays(5));
        futureBookingDto.setEnd(LocalDateTime.now().plusDays(10));
        futureBookingDto.setItemId(createdItemDto.getId());
        BookingResponseDto responseFutureBooking = bookingService.createBooking(otherUserDto.getId(), futureBookingDto);

        BookingDto currentBookingDto = new BookingDto();
        currentBookingDto.setStart(LocalDateTime.now().minusDays(1));
        currentBookingDto.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingDto.setItemId(createdItemDto.getId());
        BookingResponseDto responseCurrentBooking = bookingService.createBooking(otherUserDto.getId(), currentBookingDto);


        List<BookingResponseDto> bookingsPast = bookingService.getBookingsByUser(otherUserDto.getId(),
                State.PAST.name(), 0, 10);
        assertEquals(responsePastBooking, bookingsPast.get(0), "Вещь в результате поиска в состоянии PAST не совпадает.");
        assertEquals(1, bookingsPast.size(), "Размер полученного списка вещей в состоянии PAST не совпадает.");

        List<BookingResponseDto> bookingsFuture = bookingService.getBookingsByUser(otherUserDto.getId(),
                State.FUTURE.name(), 0, 10);
        assertEquals(responseFutureBooking, bookingsFuture.get(0), "Вещь в результате поиска в состоянии FUTURE не совпадает.");
        assertEquals(1, bookingsFuture.size(), "Размер полученного списка вещей в состоянии FUTURE не совпадает.");

        List<BookingResponseDto> bookingsCurrent = bookingService.getBookingsByUser(otherUserDto.getId(),
                State.CURRENT.name(), 0, 10);
        assertEquals(responseCurrentBooking, bookingsCurrent.get(0), "Вещь в результате поиска в состоянии CURRENT не совпадает.");
        assertEquals(1, bookingsCurrent.size(), "Размер полученного списка вещей в состоянии CURRENT не совпадает.");
    }

    @Test
    void getBookingsByUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.getBookingsByUser(100L, State.ALL.name(), 0, 10));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void getBookingsByOwner() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        BookingResponseDto responseBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);
        List<BookingResponseDto> bookingsWaiting = bookingService.getBookingsByOwner(createdUser.getId(),
                State.WAITING.name(), 0, 10);
        assertEquals(responseBooking, bookingsWaiting.get(0), "Вещь в результате поиска в состоянии WAITING не совпадает.");
        assertEquals(1, bookingsWaiting.size(), "Размер полученного списка вещей в состоянии WAITING не совпадает.");

        BookingResponseDto rejectedBooking = bookingService.approveBooking(createdUser.getId(), responseBooking.getId(),
                false);
        List<BookingResponseDto> bookingsRejected = bookingService.getBookingsByOwner(createdUser.getId(),
                State.REJECTED.name(), 0, 10);
        assertEquals(rejectedBooking, bookingsRejected.get(0), "Вещь в результате поиска в состоянии REJECTED не совпадает.");
        assertEquals(1, bookingsRejected.size(), "Размер полученного списка вещей в состоянии REJECTED не совпадает.");

        List<BookingResponseDto> bookingsAll = bookingService.getBookingsByOwner(createdUser.getId(),
                State.ALL.name(), 0, 10);
        assertEquals(rejectedBooking, bookingsAll.get(0), "Вещь в результате поиска в любом состоянии не совпадает.");
        assertEquals(1, bookingsAll.size(), "Размер полученного списка вещей в любом состоянии не совпадает.");
    }

    @Test
    void getBookingsByOwnerPastFutureCurrent() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        bookingDto.setItemId(createdItemDto.getId());
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        bookingDto.setStart(LocalDateTime.now().minusDays(10));
        bookingDto.setEnd(LocalDateTime.now().minusDays(5));
        BookingResponseDto responsePastBooking = bookingService.createBooking(otherUserDto.getId(), bookingDto);

        BookingDto futureBookingDto = new BookingDto();
        futureBookingDto.setStart(LocalDateTime.now().plusDays(5));
        futureBookingDto.setEnd(LocalDateTime.now().plusDays(10));
        futureBookingDto.setItemId(createdItemDto.getId());
        BookingResponseDto responseFutureBooking = bookingService.createBooking(otherUserDto.getId(), futureBookingDto);

        BookingDto currentBookingDto = new BookingDto();
        currentBookingDto.setStart(LocalDateTime.now().minusDays(1));
        currentBookingDto.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingDto.setItemId(createdItemDto.getId());
        BookingResponseDto responseCurrentBooking = bookingService.createBooking(otherUserDto.getId(), currentBookingDto);


        List<BookingResponseDto> bookingsPast = bookingService.getBookingsByOwner(createdUser.getId(),
                State.PAST.name(), 0, 10);
        assertEquals(responsePastBooking, bookingsPast.get(0), "Вещь в результате поиска в состоянии PAST не совпадает.");
        assertEquals(1, bookingsPast.size(), "Размер полученного списка вещей в состоянии PAST не совпадает.");

        List<BookingResponseDto> bookingsFuture = bookingService.getBookingsByOwner(createdUser.getId(),
                State.FUTURE.name(), 0, 10);
        assertEquals(responseFutureBooking, bookingsFuture.get(0), "Вещь в результате поиска в состоянии FUTURE не совпадает.");
        assertEquals(1, bookingsFuture.size(), "Размер полученного списка вещей в состоянии FUTURE не совпадает.");

        List<BookingResponseDto> bookingsCurrent = bookingService.getBookingsByOwner(createdUser.getId(),
                State.CURRENT.name(), 0, 10);
        assertEquals(responseCurrentBooking, bookingsCurrent.get(0), "Вещь в результате поиска в состоянии CURRENT не совпадает.");
        assertEquals(1, bookingsCurrent.size(), "Размер полученного списка вещей в состоянии CURRENT не совпадает.");
    }

    @Test
    void getBookingsByOwnerNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> bookingService.getBookingsByOwner(100L, State.ALL.name(), 0, 10));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }
}
