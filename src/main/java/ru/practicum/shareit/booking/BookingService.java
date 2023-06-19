package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, long bookerId);

    BookingDto approveBooking(long bookingId, boolean approved, long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getBookingByUserIdAndState(long userId, String state);

    List<BookingDto> getOwnerItemsByState(long userId, String state);

    Booking findBookingById(long bookingId);
}