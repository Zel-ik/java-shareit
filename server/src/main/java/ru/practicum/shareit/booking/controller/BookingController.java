package ru.practicum.shareit.booking.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.BookingDtoFrom;
import ru.practicum.shareit.booking.model.dto.BookingDtoTo;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoTo createBooking(@RequestBody BookingDtoFrom bookingDtoFrom,
                                      @RequestHeader(X_SHARER_USER_ID) long userId) {
        return bookingService.createBooking(bookingDtoFrom, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoTo makeApprovedBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
                                            @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        return bookingService.makeApprovedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoTo getBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
                                   @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoTo> getUserBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(name = "from", defaultValue = "0") int from,
                                              @RequestParam(name = "size", defaultValue = "10") int size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoTo> getOwnerBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(name = "from", defaultValue = "0") int from,
                                               @RequestParam(name = "size", defaultValue = "10") int size) {
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}
