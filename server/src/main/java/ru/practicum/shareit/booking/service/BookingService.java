package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.dto.BookingDtoFrom;
import ru.practicum.shareit.booking.model.dto.BookingDtoTo;

import java.util.List;

public interface BookingService {
    BookingDtoTo createBooking(BookingDtoFrom bookingDtoFrom, long userId);

    BookingDtoTo makeApprovedBooking(long userId, long bookingId, boolean approved);

    BookingDtoTo getBooking(long userId, long bookingId);

    List<BookingDtoTo> getUserBookings(long userId, String state, int from, int size);

    List<BookingDtoTo> getOwnerBookings(long userId, String state, int from, int size);
}
