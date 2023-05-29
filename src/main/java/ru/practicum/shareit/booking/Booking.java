package ru.practicum.shareit.booking;

import ch.qos.logback.core.status.Status;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private Long id;
    private LocalDate start; // дата начала периода бронирования
    private LocalDate end; // дата конца периода бронирования
    private Item item; // Что бронирует
    private User booker; // Кто бронирует
    private Status status; // статус бронирования
}
