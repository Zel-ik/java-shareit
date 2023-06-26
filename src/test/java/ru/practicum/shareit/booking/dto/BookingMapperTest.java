package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingMapperTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    private BookingDto bookingDto;

    @BeforeEach
    void createModel() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        User user = new User(1L, "Artem", "artem@gmail.com");
        User anotherUser = new User(2L, "nick", "nick@mail.com");

        Item item = new Item(1L, "name", "norm", true, user, null);
        Booking booking = new Booking(1L, start, end,
                item, anotherUser, null);
        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void dtoTest() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
    }
}