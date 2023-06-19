package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId,
                                                                          LocalDateTime start,
                                                                          LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus state);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long userId, BookingStatus status);

    List<Booking> findByItemIdAndStartBeforeAndStatusOrderByStartDesc(long itemId, LocalDateTime time, BookingStatus status);

    List<Booking> findByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime time, BookingStatus status);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(long commentatorId, long itemId, LocalDateTime time);
}