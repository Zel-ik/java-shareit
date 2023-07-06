package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.startDate DESC")
    List<Booking> findBookingsByBookerId(@Param("bookerId") Long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.endDate < :currentDate " +
            "ORDER BY b.startDate DESC")
    List<Booking> findPastBookingsByBookerId(@Param("bookerId") Long bookerId,
                                             @Param("currentDate") LocalDateTime currentDate,
                                             Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.startDate >= :currentDate " +
            "ORDER BY b.startDate DESC")
    List<Booking> findFutureBookingsByBookerId(@Param("bookerId") Long bookerId,
                                               @Param("currentDate") LocalDateTime currentDate,
                                               Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = :waitingStatus " +
            "ORDER BY b.startDate DESC")
    List<Booking> findWaitingBookingsByBookerId(@Param("bookerId") Long bookerId,
                                                @Param("waitingStatus") BookingStatus waitingStatus,
                                                Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = :rejectedStatus " +
            "ORDER BY b.startDate DESC")
    List<Booking> findRejectedBookingsByBookerId(@Param("bookerId") Long bookerId,
                                                 @Param("rejectedStatus") BookingStatus rejectedStatus,
                                                 Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.startDate <= :currentDate " +
            "AND b.endDate >= :currentDate ORDER BY b.startDate DESC")
    List<Booking> findCurrentBookingsByBookerId(@Param("bookerId") Long bookerId,
                                                @Param("currentDate") LocalDateTime currentDate,
                                                Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :ownerId ORDER BY b.startDate DESC")
    List<Booking> findByItemOwnerIdOrderByStartDateDesc(@Param("ownerId") Long ownerId,
                                                        Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :ownerId AND b.status = :status ORDER BY b.startDate DESC")
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDateDesc(@Param("ownerId") Long ownerId,
                                                                 @Param("status") BookingStatus status,
                                                                 Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :ownerId AND b.endDate < :currentDate " +
            "ORDER BY b.startDate DESC")
    List<Booking> findPastBookingsByItemOwnerIdOrderByStartDateDesc(@Param("ownerId") Long ownerId,
                                                                    @Param("currentDate") LocalDateTime currentDate,
                                                                    Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :ownerId AND b.startDate >= :currentDate " +
            "ORDER BY b.startDate DESC")
    List<Booking> findFutureBookingsByItemOwnerIdOrderByStartDateDesc(@Param("ownerId") Long ownerId,
                                                                      @Param("currentDate") LocalDateTime currentDate,
                                                                      Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :ownerId AND b.startDate <= :currentDate " +
            "AND b.endDate >= :currentDate ORDER BY b.startDate DESC")
    List<Booking> findCurrentBookingsByItemOwnerIdOrderByStartDateDesc(@Param("ownerId") Long ownerId,
                                                                       @Param("currentDate") LocalDateTime currentDate,
                                                                       Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND b.startDate < :currentDate ORDER BY b.startDate DESC")
    List<Booking> findLastBookingsByItemId(@Param("itemId") Long itemId,
                                           @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND b.startDate > :currentDate ORDER BY b.startDate")
    List<Booking> findNextBookingsByItemId(@Param("itemId") Long itemId,
                                           @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.item.id = :itemId " +
            "AND b.endDate < :currentDate ORDER BY b.startDate DESC")
    List<Booking> findPastBookingsByBookerIdAndItemId(@Param("bookerId") Long bookerId,
                                                      @Param("itemId") Long itemId,
                                                      @Param("currentDate") LocalDateTime currentDate);
}
