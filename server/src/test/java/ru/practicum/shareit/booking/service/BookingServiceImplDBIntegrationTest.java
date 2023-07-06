package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoTo;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BookingServiceImplDBIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        User booker = new User();
        booker.setEmail("Email@mail.ru");
        booker.setName("Jon");
        User owner = new User();
        owner.setEmail("Pes@mail.ru");
        owner.setName("Sina");
        userRepository.saveAll(Arrays.asList(booker, owner));

        Item item = new Item();

        item.setUser(owner);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setComments(new ArrayList<>());

        itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = new Booking(1L, now.minusDays(1), now.plusDays(1), BookingStatus.APPROVED, booker, item);
        Booking booking2 = new Booking(2L, now, now.plusDays(2), BookingStatus.APPROVED, booker, item);
        Booking booking3 = new Booking(3L, now.plusDays(3), now.plusDays(5), BookingStatus.APPROVED, booker, item);
        Booking booking4 = new Booking(4L, now.plusDays(7), now.plusDays(8), BookingStatus.WAITING, booker, item);
        Booking booking5 = new Booking(5L, now.minusDays(2), now.minusDays(1), BookingStatus.REJECTED, booker, item);
        bookingRepository.saveAll(Arrays.asList(booking1, booking2, booking3, booking4, booking5));
    }

    @Test
    void testGetUserBookings() {
        long userId = userRepository.findByEmail("Email@mail.ru").get().getId();
        String state = "ALL";
        int from = 0;
        int size = 10;

        List<BookingDtoTo> result = bookingService.getUserBookings(userId, state, from, size);

        assertEquals(5, result.size());

        BookingDtoTo bookingDtoTo = result.get(0);
        assertNotNull(bookingDtoTo.getStart());
        assertNotNull(bookingDtoTo.getEnd());
        assertNotNull(bookingDtoTo.getStatus());
        assertNotNull(bookingDtoTo.getBooker());
        assertNotNull(bookingDtoTo.getItem());
    }
}