package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody @Valid BookingDto bookingDto,
                                    @RequestHeader(USER_ID_HEADER) long bookerId) {
        BookingDto newBookingDto = bookingService.createBooking(bookingDto, bookerId);
        log.info("Создан новый запрос на бронирование: {}", newBookingDto);
        return newBookingDto;
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@PathVariable long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader(USER_ID_HEADER) long userId) {
        BookingDto bookingDto = bookingService.approveBooking(bookingId, approved, userId);
        log.info("Статус заявки на бронирование вещи с ID={} изменен на: {}", bookingId, approved);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Пользователем с ID={} запрошена заявка на бронирование с ID={}", userId, bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getUserBookingsByState(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Пользователем с ID={} запрошены заявки на бронирование со статусом: {}", userId, state);
        return bookingService.getBookingByUserIdAndState(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getOwnerItemsByState(@RequestParam(defaultValue = "ALL") String state,
                                                 @RequestHeader(USER_ID_HEADER) long ownerId) {
        log.info("Пользователем с ID={} запросил список своих вещей со статусом: {}", ownerId, state);
        return bookingService.getOwnerItemsByState(ownerId, state);
    }
}