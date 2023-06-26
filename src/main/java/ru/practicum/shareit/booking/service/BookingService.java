package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoIn;
import ru.practicum.shareit.constans.State;

import java.util.Collection;

public interface BookingService {
    BookingDto saveBooking(BookingDtoIn booking, Long userId);

    BookingDto confirmBooking(Boolean approve, Long id, Long userId);

    BookingDto getBookingById(Long id, Long userId);

    Collection<BookingDto> getAllBooking(Long userId, State state, Integer from, Integer size);

    Collection<BookingDto> getAllBookingForItems(Long userId, State state, Integer from, Integer size);
}