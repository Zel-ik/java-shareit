package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constans.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);//waiting and rejected

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long userId);//для ALL

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime time);//past

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable);

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime time);//future

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable);//future

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime time,
                                                                                     LocalDateTime currentTime); //для Current

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime time,
                                                                               LocalDateTime currentTime, Pageable pageable);

    Collection<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId);//all

    Page<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Collection<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);//waiting and rejected

    Page<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status, Pageable pageable);

    Collection<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time);//past

    Page<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time, Pageable pageable);

    Collection<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime time);//future

    Page<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime time, Pageable pageable);//future

    Collection<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime time,
                                                                                       LocalDateTime currentTime);//all

    Page<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime time,
                                                                                 LocalDateTime currentTime, Pageable pageable);

    Collection<Booking> findAllByItem_IdAndItem_Owner_IdAndStatusNotOrderByStartAsc(Long itemId, Long userId, Status status);

    Collection<Booking> findAllByItem_IdInOrderByStartAsc(Set<Long> itemsId);

    Collection<Booking> findAllByItem_IdAndBooker_IdAndStatusNotAndStartBefore(Long itemId, Long userId, Status status, LocalDateTime time);
}