package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User booker;
    private User owner;
    private Item item1;
    private Item item2;


    @BeforeEach
    public void setUp() {
        booker = new User();
        booker.setEmail("test@example.com");
        booker.setName("Test User");
        testEntityManager.persist(booker);


        owner = new User();
        owner.setEmail("owner@example.com");
        owner.setName("Test Owner");
        testEntityManager.persist(owner);

        item1 = new Item();
        item1.setUser(owner);
        item1.setName("Test Item");
        item1.setDescription("Test Description");
        item1.setAvailable(true);
        testEntityManager.persist(item1);

        item2 = new Item();
        item2.setUser(owner);
        item2.setName("Test Item");
        item2.setDescription("Test Description");
        item2.setAvailable(true);
        testEntityManager.persist(item2);
    }

    @AfterEach
    public void tearDown() {
        testEntityManager.clear();
    }

    @Test
    void findBookingsByBookerId_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.APPROVED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);

        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findBookingsByBookerId(booker.getId(), PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking2, booking1);
    }

    @Test
    void findPastBookingsByBookerId_shouldReturnCorrectBookings() {

        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), BookingStatus.APPROVED);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), BookingStatus.APPROVED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findPastBookingsByBookerId(booker.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking2, booking1);
    }

    @Test
    void findFutureBookingsByBookerId_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.WAITING);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findFutureBookingsByBookerId(booker.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking2, booking1);
    }

    @Test
    void findWaitingBookingsByBookerId_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.WAITING);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findWaitingBookingsByBookerId(booker.getId(), BookingStatus.WAITING, PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking2);
    }

    @Test
    void findRejectedBookingsByBookerId_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.REJECTED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findRejectedBookingsByBookerId(booker.getId(), BookingStatus.REJECTED, PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking2);
    }

    @Test
    void findCurrentBookingsByBookerId_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), BookingStatus.APPROVED);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findCurrentBookingsByBookerId(booker.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking1);
    }

    @Test
    void findByItemOwnerIdOrderByStartDateDesc_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), BookingStatus.APPROVED);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), BookingStatus.APPROVED);
        Booking booking3 = new Booking(booker, item1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), BookingStatus.APPROVED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.persist(booking3);

        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDateDesc(owner.getId(), PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking3, booking2, booking1);
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDateDesc_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), BookingStatus.APPROVED);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), BookingStatus.WAITING);
        Booking booking3 = new Booking(booker, item1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), BookingStatus.APPROVED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.persist(booking3);
        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDateDesc(owner.getId(), BookingStatus.APPROVED, PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking3, booking1);
    }

    @Test
    void findPastBookingsByItemOwnerIdOrderByStartDateDesc_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), BookingStatus.APPROVED);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), BookingStatus.APPROVED);
        Booking booking3 = new Booking(booker, item1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), BookingStatus.APPROVED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.persist(booking3);
        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findPastBookingsByItemOwnerIdOrderByStartDateDesc(owner.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking2, booking1);
    }

    @Test
    void findFutureBookingsByItemOwnerIdOrderByStartDateDesc_shouldReturnCorrectBookings() {
        Booking booking1 = new Booking(booker, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
        Booking booking2 = new Booking(booker, item2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.WAITING);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.flush();

        List<Booking> bookings = bookingRepository.findFutureBookingsByItemOwnerIdOrderByStartDateDesc(owner.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings).containsExactly(booking2, booking1);
    }
}

