package ru.practicum.shareit.booking.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    Booking bookingDtoToBooking(BookingDto bookingDto);

    @Mapping(source = "item", target = "item", qualifiedByName = "mapItemDto")
    BookingResponseDto bookingToBookingResponseDto(Booking booking);

    @Named("mapItemDto")
    ItemBookingDto mapItemDto(Item item);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingForItemDto bookingToBookingForItemDto(Booking booking);
}
