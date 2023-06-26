package ru.practicum.shareit.booking.service;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constans.State;
import ru.practicum.shareit.constans.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class BookingServiceImplIntegrationTest {
    private final BookingServiceImpl bookingService;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;

    @Test
    void getAllBookingTest() {
        User itemOwner = createUser("1");
        User booker = createUser("2");
        userRepository.saveAll(List.of(itemOwner, booker));
        Long bookerId = booker.getId();
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", itemOwner);
        Item item3 = createItem("3", itemOwner);
        Item item4 = createItem("4", itemOwner);
        Item item5 = createItem("5", itemOwner);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));
        Booking pastBooking = createPastBooking(item1, booker);
        Booking currentBooking = createCurrentBooking(item2, booker);
        Booking futureBooking = createFutureBooking(item3, booker);
        Booking rejectedBooking = createRejectedBooking(item5, booker);
        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));

        Collection<BookingDto> all = bookingService.getAllBooking(bookerId, State.ALL, 0, 10);
        Collection<BookingDto> past = bookingService.getAllBooking(bookerId, State.PAST, 0, 10);
        Collection<BookingDto> current = bookingService.getAllBooking(bookerId, State.CURRENT, 0, 10);
        Collection<BookingDto> future = bookingService.getAllBooking(bookerId, State.FUTURE, 0, 10);
        Collection<BookingDto> waiting = bookingService.getAllBooking(bookerId, State.WAITING, 0, 10);
        Collection<BookingDto> rejected = bookingService.getAllBooking(bookerId, State.REJECTED, 0, 10);

        assertThat(all.size()).isEqualTo(4);
        assertThat(past.size()).isEqualTo(1);
        assertThat(past).contains(BookingMapper.toBookingDto(pastBooking));
        assertThat(current.size()).isEqualTo(2);
        assertThat(current).contains(BookingMapper.toBookingDto(currentBooking));
        assertThat(current).contains(BookingMapper.toBookingDto(rejectedBooking));
        assertThat(future.size()).isEqualTo(1);
        assertThat(future).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(waiting.size()).isEqualTo(1);
        assertThat(waiting).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(rejected.size()).isEqualTo(1);
        assertThat(rejected).contains(BookingMapper.toBookingDto(rejectedBooking));
    }

    @Test
    void getAllByItemOwnerIdTestWithPag() {
        User itemOwner = createUser("1");
        User booker = createUser("2");
        userRepository.saveAll(List.of(itemOwner, booker));
        Long ownerId = itemOwner.getId();
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", itemOwner);
        Item item3 = createItem("3", itemOwner);
        Item item4 = createItem("4", itemOwner);
        Item item5 = createItem("5", itemOwner);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));
        Booking pastBooking = createPastBooking(item1, booker);
        Booking currentBooking = createCurrentBooking(item2, booker);
        Booking futureBooking = createFutureBooking(item3, booker);
        Booking rejectedBooking = createRejectedBooking(item5, booker);
        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));

        Collection<BookingDto> all = bookingService.getAllBookingForItems(ownerId, State.ALL, 0, 10);
        Collection<BookingDto> past = bookingService.getAllBookingForItems(ownerId, State.PAST, 0, 10);
        Collection<BookingDto> current = bookingService.getAllBookingForItems(ownerId, State.CURRENT, 0, 10);
        Collection<BookingDto> future = bookingService.getAllBookingForItems(ownerId, State.FUTURE, 0, 10);
        Collection<BookingDto> waiting = bookingService.getAllBookingForItems(ownerId, State.WAITING, 0, 10);
        Collection<BookingDto> rejected = bookingService.getAllBookingForItems(ownerId, State.REJECTED, 0, 10);

        assertThat(all.size()).isEqualTo(4);
        assertThat(past.size()).isEqualTo(1);
        assertThat(past).contains(BookingMapper.toBookingDto(pastBooking));
        assertThat(current.size()).isEqualTo(2);
        assertThat(current).contains(BookingMapper.toBookingDto(currentBooking));
        assertThat(current).contains(BookingMapper.toBookingDto(rejectedBooking));
        assertThat(future.size()).isEqualTo(1);
        assertThat(future).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(waiting.size()).isEqualTo(1);
        assertThat(waiting).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(rejected.size()).isEqualTo(1);
        assertThat(rejected).contains(BookingMapper.toBookingDto(rejectedBooking));
    }

    @Test
    void getAllByItemOwnerIdTestWithoutPag() {
        User itemOwner = createUser("1");
        User booker = createUser("2");
        userRepository.saveAll(List.of(itemOwner, booker));
        Long ownerId = itemOwner.getId();
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", itemOwner);
        Item item3 = createItem("3", itemOwner);
        Item item4 = createItem("4", itemOwner);
        Item item5 = createItem("5", itemOwner);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));
        Booking pastBooking = createPastBooking(item1, booker);
        Booking currentBooking = createCurrentBooking(item2, booker);
        Booking futureBooking = createFutureBooking(item3, booker);
        Booking rejectedBooking = createRejectedBooking(item5, booker);
        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));

        Collection<BookingDto> all = bookingService.getAllBookingForItems(ownerId, State.ALL, null, null);
        Collection<BookingDto> past = bookingService.getAllBookingForItems(ownerId, State.PAST, null, null);
        Collection<BookingDto> current = bookingService.getAllBookingForItems(ownerId, State.CURRENT, null, null);
        Collection<BookingDto> future = bookingService.getAllBookingForItems(ownerId, State.FUTURE, null, null);
        Collection<BookingDto> waiting = bookingService.getAllBookingForItems(ownerId, State.WAITING, null, null);
        Collection<BookingDto> rejected = bookingService.getAllBookingForItems(ownerId, State.REJECTED, null, null);

        assertThat(all.size()).isEqualTo(4);
        assertThat(past.size()).isEqualTo(1);
        assertThat(past).contains(BookingMapper.toBookingDto(pastBooking));
        assertThat(current.size()).isEqualTo(2);
        assertThat(current).contains(BookingMapper.toBookingDto(currentBooking));
        assertThat(current).contains(BookingMapper.toBookingDto(rejectedBooking));
        assertThat(future.size()).isEqualTo(1);
        assertThat(future).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(waiting.size()).isEqualTo(1);
        assertThat(waiting).contains(BookingMapper.toBookingDto(futureBooking));
        assertThat(rejected.size()).isEqualTo(1);
        assertThat(rejected).contains(BookingMapper.toBookingDto(rejectedBooking));
    }

    User createUser(String userPostfix) {
        return User.builder()
                .name(String.format("Name%s", userPostfix))
                .email(String.format("email@email%s.ru", userPostfix))
                .build();
    }

    Item createItem(String itemPostfix, User itemOwner) {
        return Item.builder()
                .name(String.format("Item%s", itemPostfix))
                .description(String.format("ItemDescription%s", itemPostfix))
                .available(true)
                .owner(itemOwner).build();
    }

    Booking createPastBooking(Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.CANCELED)
                .build();

    }

    private Booking createCurrentBooking(Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
    }

    Booking createFutureBooking(Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    Booking createRejectedBooking(Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(Status.REJECTED)
                .build();
    }
}