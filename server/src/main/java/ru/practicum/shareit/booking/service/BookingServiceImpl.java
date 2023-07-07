package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.OperationNotAllowed;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingDto bookingDto) {
        log.info("createBooking: {}, userId={}", bookingDto, userId);
        User booker = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        Long bookerId = booker.getId();
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("item with id={} not exist", itemId);
            throw new EntityNotExistException(String.format("Вещь с id=%d не существует.", itemId));
        });
        if (!item.getAvailable()) {
            log.warn("item with id={} not available", itemId);
            throw new OperationNotAllowed(String.format("Вещь с id=%d недоступна.", itemId));
        }
        Booking booking = BookingMapper.INSTANCE.bookingDtoToBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        if (bookerId.equals(item.getOwner().getId())) {
            log.warn("item can't be booked by owner");
            throw new EntityNotExistException("Вещь не может быть забронирована владельцем.");
        }
        return BookingMapper.INSTANCE.bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("approveBooking: userId={}, booking={}, approved={}", userId, bookingId, approved);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("booking with id={} not exist", bookingId);
            throw new EntityNotExistException(String.format("Бронирование с id=%d не существует.", bookingId));
        });
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("user with id={} is not owner", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не является владельцем вещи.", userId));
        }
        if (booking.getStatus() == BookingStatus.WAITING) {
            BookingStatus status = (approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            booking.setStatus(status);
        } else {
            log.warn("booking with not WAITING status");
            throw new OperationNotAllowed("Бронирование не в статусе ожидания подтверждения.");
        }
        return BookingMapper.INSTANCE.bookingToBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        log.info("getBookingById: userId={}, booking={}", userId, bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("booking with id={} not exist", bookingId);
            throw new EntityNotExistException(String.format("Бронирование с id=%d не существует.", bookingId));
        });
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            log.warn("user with id={} is not allowed to get this booking", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не может просматривать это бронирование.", userId));
        }
        return BookingMapper.INSTANCE.bookingToBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(Long userId, String state, Integer from, Integer size) {
        log.info("getBookingsByUser: userId={}, state={}", userId, state);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        State bookingState;
        bookingState = State.valueOf(state.toUpperCase());
        PageRequest pageRequest = new CustomPageRequest(from, size);
        List<Booking> bookingList;
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerCurrentDate(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                bookingList = new ArrayList<>();
        }
        return bookingList.stream()
                .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long userId, String state, Integer from, Integer size) {
        log.info("getBookingsByOwner: userId={}, state={}", userId, state);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        State bookingState;
        bookingState = State.valueOf(state.toUpperCase());
        PageRequest pageRequest = new CustomPageRequest(from, size);
        List<Booking> bookingList;
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findAllByOwnerId(userId, pageRequest);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByOwnerCurrentDate(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                bookingList = new ArrayList<>();
        }
        return bookingList.stream()
                .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                .collect(Collectors.toList());
    }
}
