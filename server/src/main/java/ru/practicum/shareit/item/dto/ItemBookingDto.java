package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemBookingDto extends ItemDto {
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
