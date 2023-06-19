package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public BookingDto createBooking(BookingDto bookingDto, long bookerId) {
        Item item = itemService.findItemById(bookingDto.getItemId());
        User booker = userService.findUserById(bookerId);
        if (!item.getAvailable()) {
            throw new InvalidArgumentException("Вещь недоступна для бронирования");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new InvalidArgumentException("Конец бронирования должен быть позже времени начала бронирования");
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new InvalidArgumentException("Время начала и конца бронирования не должны совпадать");
        }
        if (bookerId == item.getOwner().getId()) {
            throw new NoSuchElementException("Бронирование собственных вещей невозможно");
        }
        Booking newBooking = BookingMapper.toBooking(bookingDto);
        newBooking.setBooker(booker);
        newBooking.setItem(item);
        newBooking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(long bookingId, boolean approved, long userId) {
        userService.checkUserExistence(userId);
        Booking booking = findBookingById(bookingId);
        if (userId != booking.getItem().getOwner().getId()) {
            throw new NoSuchElementException("Только владелец вещи может подтверждать/отклонять бронирование");
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new InvalidArgumentException(String.format("Заявка на бронирование уже %s владельцем", booking.getStatus()));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        userService.checkUserExistence(userId);
        if (userId == findBookingById(bookingId).getBooker().getId() ||
                userId == findBookingById(bookingId).getItem().getOwner().getId()) {
            return BookingMapper.toBookingDto(findBookingById(bookingId));
        } else {
            throw new NoSuchElementException("Получение данных о бронировании может только владелец или арендатор вещи");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingByUserIdAndState(long userId, String state) {
        userService.checkUserExistence(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new InvalidArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getOwnerItemsByState(long userId, String state) {
        userService.checkUserExistence(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new InvalidArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Бронирование с ID=%d не существует", bookingId)));
    }
}