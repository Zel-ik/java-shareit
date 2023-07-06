package ru.practicum.shareit.item.model.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ItemMapperImpl implements ItemMapper {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto mapTo(Item item, long userId) {
        BookingDtoForItem bookingDtoLast = null;
        BookingDtoForItem bookingDtoNext = null;
        Optional<Booking> bookingLast = bookingRepository.findLastBookingsByItemId(item.getId(), LocalDateTime.now())
                .stream().findFirst();
        Optional<Booking> bookingNext = bookingRepository.findNextBookingsByItemId(item.getId(), LocalDateTime.now())
                .stream().findFirst();

        if (item.getUser().getId() == userId) {
            if (bookingLast.isPresent()) {
                bookingDtoLast = new BookingDtoForItem(bookingLast.get().getId(), bookingLast.get().getBooker().getId());
            }
            if (bookingNext.isPresent()) {
                bookingDtoNext = new BookingDtoForItem(bookingNext.get().getId(), bookingNext.get().getBooker().getId());
            }
        }
        List<CommentDto> commentDtoList = commentMapper.getComments(item.getId());

        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), bookingDtoLast,
                bookingDtoNext, commentDtoList, item.getItemRequest() != null ? item.getItemRequest().getId() : null);
    }

    @Override
    public Item mapFrom(ItemDto entity, long userId) {
        Item item = new Item(entity.getId(),
                userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("Невозможно" +
                        " создать %s так как пользователь с идентификатором %d не существует", entity.getName(), userId))),
                entity.getName(), entity.getDescription(), entity.getAvailable(), new ArrayList<>());
        if (entity.getRequestId() != null) {
            item.setItemRequest(itemRequestRepository.findById(entity.getRequestId()).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Request with id %d does not exist", entity.getRequestId()))));
        }
        return item;
    }

    @Override
    public Item mapFrom(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() == null && itemDto.getDescription() == null && itemDto.getAvailable() == null) {
            throw new ConflictException("Item is empty");
        }
        return item;
    }
}
