package ru.practicum.shareit.booking.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.model.dto.UserDtoForBooking;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoTo {
    private long id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime start;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime end;
    private BookingStatus status;
    private UserDtoForBooking booker;
    private ItemDtoForBooking item;
}