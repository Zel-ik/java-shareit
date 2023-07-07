package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jacksonTesterDto;
    @Autowired
    private JacksonTester<BookingResponseDto> jacksonTesterResponseDto;
    @Autowired
    private JacksonTester<BookingForItemDto> jacksonTesterDtoForItem;
    private BookingDto bookingDto;
    private BookingForItemDto bookingForItemDto;
    private ItemDto itemDto;
    private UserDto userDto;



    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(2023, 1, 10, 14, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 2, 10, 14, 0, 0));
        bookingDto.setItemId(1L);
        bookingDto.setId(1L);

        bookingForItemDto = new BookingForItemDto();
        bookingForItemDto.setId(1L);
        bookingForItemDto.setStart(LocalDateTime.of(2023, 1, 10, 14, 0, 0));
        bookingForItemDto.setEnd(LocalDateTime.of(2023, 2, 10, 14, 0, 0));
        bookingForItemDto.setBookerId(1L);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("test");
        userDto.setEmail("test@test.ru");

        itemDto = new ItemBookingDto();
        itemDto.setId(1L);
        itemDto.setName("test");
        itemDto.setDescription("test description");
        itemDto.setAvailable(true);
    }

    @Test
    void testJsonBookingDto() throws Exception {
        JsonContent<BookingDto> result = jacksonTesterDto.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-10T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-02-10T14:00:00");
    }

    @Test
    void testJsonBookingResponseDto() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(LocalDateTime.of(2023, 1, 10, 14, 0, 0));
        bookingResponseDto.setEnd(LocalDateTime.of(2023, 2, 10, 14, 0, 0));
        bookingResponseDto.setBooker(userDto);
        bookingResponseDto.setItem(itemDto);
        bookingResponseDto.setStatus(BookingStatus.WAITING);

        JsonContent<BookingResponseDto> result = jacksonTesterResponseDto.write(bookingResponseDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-10T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-02-10T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testJsonBookingForItemDto() throws Exception {
        JsonContent<BookingForItemDto> result = jacksonTesterDtoForItem.write(bookingForItemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-10T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-02-10T14:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}
