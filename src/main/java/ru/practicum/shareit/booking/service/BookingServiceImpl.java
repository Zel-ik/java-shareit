package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constans.State;
import ru.practicum.shareit.constans.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto saveBooking(BookingDtoIn booking, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя не существует"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Предмета не существует"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна к бронированию");
        }
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Вещь не доступна к бронированию");
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Вещь не может быть забронирована, так как пользователь хояин вещи");
        }
        Booking bookingIn = BookingMapper.toBooking(booking, user, item);
        return BookingMapper.toBookingDto(bookingRepository.save(bookingIn));
    }

    @Override
    public BookingDto confirmBooking(Boolean approve, Long id, Long userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет данных о бронировании"));
        Item item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Данный пользователь не хозяин вещи");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("Статус уже APPROVED");
        }
        if (approve == true) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long id, Long userId) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет данных о бронировании"));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundException("Данный пользователь не обладает доступом к просмотру");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getAllBooking(Long userId, State state, Integer from, Integer size) {
        checkUser(userId);
        Collection<Booking> bookings;
        if (Objects.nonNull(from) && Objects.nonNull(size)) {
            if (from < 0 || size <= 0) {
                throw new ValidationException("Значения не могут быть отрицательными");
            }
            int pageNumber = from / size;
            Pageable pagination = PageRequest.of(pageNumber, size);
            bookings = getAllBookingWithPag(userId, state, pagination);
        } else {
            bookings = getAllBookingWithoutPag(userId, state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getAllBookingForItems(Long userId, State state, Integer from, Integer size) {
        checkUser(userId);
        Collection<Booking> bookings;
        if (Objects.nonNull(from) && Objects.nonNull(size)) {
            if (from < 0 || size <= 0) {
                throw new ValidationException("Значения не могут быть отрицательными");
            }
            int pageNumber = from / size;
            Pageable pagination = PageRequest.of(pageNumber, size);
            bookings = getAllBookingForItemsWithPag(userId, state, pagination);
        } else {
            bookings = getAllBookingForItemsWithoutPag(userId, state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    private Collection<Booking> getAllBookingWithPag(Long userId, State state, Pageable pagination) {
        checkUser(userId);
        LocalDateTime time = LocalDateTime.now();
        Page<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pagination);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pagination);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(userId, time, time, pagination);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, time, pagination);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, time, pagination);
                break;
            default:
                throw new UnsupportedException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.getContent();
    }


    private Collection<Booking> getAllBookingWithoutPag(Long userId, State state) {
        checkUser(userId);
        Collection<Booking> bookings;
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(userId, time, time);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, time);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, time);
                break;
            default:
                throw new UnsupportedException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    private Collection<Booking> getAllBookingForItemsWithPag(Long userId, State state, Pageable pagination) {
        checkUser(userId);
        LocalDateTime time = LocalDateTime.now();
        Page<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(userId, pagination);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pagination);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time, pagination);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(userId, time, pagination);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(userId, time, pagination);
                break;
            default:
                throw new UnsupportedException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.getContent();
    }

    private Collection<Booking> getAllBookingForItemsWithoutPag(Long userId, State state) {
        checkUser(userId);
        LocalDateTime time = LocalDateTime.now();
        Collection<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(userId, time);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(userId, time);
                break;
            default:
                throw new UnsupportedException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    private void checkUser(Long userId) {
        if (userId == null) {
            throw new NotFoundException("Пользователь не указан");
        } else if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Указанный пользователь не сущетсвует");
        }
    }
}