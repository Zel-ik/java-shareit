package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constans.State;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto saveBooking(@RequestHeader(value = USER_ID) Long userId, @Valid @RequestBody BookingDtoIn booking) {
        log.info("Получен запрос 'POST /bookings'");
        return bookingService.saveBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmBooking(@RequestHeader(value = USER_ID) Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("Получен запрос 'Patch /bookings/{bookingId}'");
        return bookingService.confirmBooking(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(value = USER_ID) Long userId, @PathVariable Long bookingId) {
        log.info("Получен запрос 'Get /bookings/{bookingId}'");
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBooking(@RequestHeader(value = USER_ID) Long userId,
                                                @RequestParam(defaultValue = "ALL") State state,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) {
        log.info("Получен запрос 'Get /bookings'");
        return bookingService.getAllBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingForItems(@RequestHeader(value = USER_ID) Long userId,
                                                        @RequestParam(defaultValue = "ALL") State state,
                                                        @RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size) {
        log.info("Получен запрос 'Get /bookings/owner'");
        return bookingService.getAllBookingForItems(userId, state, from, size);
    }

}