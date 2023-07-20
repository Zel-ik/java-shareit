package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
}
