package ru.practicum.shareit.booking;

import ch.qos.logback.core.status.Status;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    Long id;
    LocalDate start; // дата начала периода бронирования
    LocalDate end; // дата конца периода бронирования
    Item item; // Что бронирует
    User booker; // Кто бронирует
    Status status; // статус бронирования
}
