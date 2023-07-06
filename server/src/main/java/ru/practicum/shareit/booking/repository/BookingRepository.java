package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus waiting, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :userId and b.start < :now and b.end > :now order by b.start DESC")
    List<Booking> findAllByBookerCurrentDate(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId order by b.start DESC")
    List<Booking> findAllByOwnerId(@Param("userId") Long userId, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.end < :now order by b.start DESC")
    List<Booking> findAllByOwnerIdAndEndIsBefore(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.start > :now order by b.start DESC")
    List<Booking> findAllByOwnerIdAndStartIsAfter(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.start < :now and b.end > :now order by b.start DESC")
    List<Booking> findAllByOwnerCurrentDate(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.status = :status order by b.start DESC")
    List<Booking> findAllByOwnerIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus waiting, Pageable pageable);

    @Query("select b from Booking b where b.item.id = :itemId and b.start < :start and b.status = 'APPROVED' order by b.end DESC")
    List<Booking> findItemLastBookings(@Param("itemId") Long itemId, @Param("start") LocalDateTime start);

    @Query("select b from Booking b where b.item.id = :itemId and b.start > :start and b.status = 'APPROVED' order by b.start")
    List<Booking> findItemNextBookings(@Param("itemId") Long itemId, @Param("start") LocalDateTime start);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, Long itemId, LocalDateTime date);
}
