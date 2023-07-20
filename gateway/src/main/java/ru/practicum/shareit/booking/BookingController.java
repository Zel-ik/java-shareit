package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.validator.StartBeforeEnd;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.ItemController.USER_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_HEADER) Long userId,
                                           @Valid @StartBeforeEnd @RequestBody BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        log.info("Approve booking with bookingId={} by user with userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(USER_HEADER) Long userId,
                                                       @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings by owner with userId={}, state {},  from={}, size={}", userId, stateParam, from, size);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }
}
