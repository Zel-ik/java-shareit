package ru.practicum.shareit.booking.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingDtoForItemTest {

    @Test
    public void testBookingDtoForItem() {
        Long expectedId = 123L;
        Long expectedBookerId = 456L;

        BookingDtoForItem bookingDto = new BookingDtoForItem(expectedId, expectedBookerId);

        assertEquals(expectedId, bookingDto.getId());
        assertEquals(expectedBookerId, bookingDto.getBookerId());

        Long newExpectedId = 789L;
        Long newExpectedBookerId = 101112L;

        bookingDto.setId(newExpectedId);
        bookingDto.setBookerId(newExpectedBookerId);

        assertEquals(newExpectedId, bookingDto.getId());
        assertEquals(newExpectedBookerId, bookingDto.getBookerId());
    }
}