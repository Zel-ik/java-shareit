package ru.practicum.shareit.booking.model.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constans.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto((booking.getItem())))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDtoIn booking, User user, Item item) {
        return Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }
}