package ru.practicum.shareit.booking.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.validateInterfaces.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingDtoFromTest {
    @Autowired
    private ObjectMapper objectMapper;

    private final Long id = 1L;
    private final LocalDateTime start = LocalDateTime.of(2023, 6, 22, 14, 30);
    private final LocalDateTime end = LocalDateTime.of(2023, 6, 22, 15, 0);
    private final BookingStatus status = BookingStatus.WAITING;
    private final Long bookerId = 2L;
    private final Long itemId = 3L;

    @Test
    public void testSerialize() throws Exception {
        BookingDtoFrom bookingDtoFrom = new BookingDtoFrom();
        bookingDtoFrom.setId(id);
        bookingDtoFrom.setStart(start);
        bookingDtoFrom.setEnd(end);
        bookingDtoFrom.setStatus(status);
        bookingDtoFrom.setBookerId(bookerId);
        bookingDtoFrom.setItemId(itemId);

        String expectedJson = "{\"id\":1,\"start\":\"2023-06-22T14:30:00\",\"end\":\"2023-06-22T15:00:00\"," +
                "\"status\":\"WAITING\",\"bookerId\":2,\"itemId\":3}";
        String actualJson = objectMapper.writeValueAsString(bookingDtoFrom);

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"start\":\"2023-06-22T14:30:00\",\"end\":\"2023-06-22T15:00:00\",\"status" +
                "\":\"WAITING\",\"bookerId\":2,\"itemId\":3}";

        BookingDtoFrom expectedBookingDtoFrom = new BookingDtoFrom();
        expectedBookingDtoFrom.setId(id);
        expectedBookingDtoFrom.setStart(start);
        expectedBookingDtoFrom.setEnd(end);
        expectedBookingDtoFrom.setStatus(status);
        expectedBookingDtoFrom.setBookerId(bookerId);
        expectedBookingDtoFrom.setItemId(itemId);

        BookingDtoFrom actualBookingDtoFrom = objectMapper.readValue(json, BookingDtoFrom.class);
        assertThat(actualBookingDtoFrom).isEqualTo(expectedBookingDtoFrom);
    }

    @Test
    public void testBookingDtoFromValidationSuccess() {
        BookingDtoFrom dto = new BookingDtoFrom(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                1L, 2L);
        Set<ConstraintViolation<BookingDtoFrom>> violations = Validation.buildDefaultValidatorFactory().getValidator()
                .validate(dto, Create.class);
        assertEquals(0, violations.size());
    }
}