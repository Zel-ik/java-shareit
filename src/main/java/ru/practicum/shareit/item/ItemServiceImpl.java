package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.findUserById(userId));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long itemId, long userId) {
        userService.checkUserExistence(userId);
        Item item = findItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (userId == item.getOwner().getId()) {
            itemDto.setLastBooking(getLastBookingInfoDto(itemId));
            itemDto.setNextBooking(getNextBookingInfoDto(itemId));
        }
        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getUserItems(long userId) {
        userService.checkUserExistence(userId);
        Map<Long, List<CommentDto>> commentsByItemId = commentRepository.findByItemOwnerId(userId)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        mapping(CommentMapper::toCommentDto, Collectors.toList())));
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .peek(item -> item.setLastBooking(getLastBookingInfoDto(item.getId())))
                .peek(item -> item.setNextBooking(getNextBookingInfoDto(item.getId())))
                .peek(item -> item.setComments(commentsByItemId.get(item.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItem(String searchRequest, long userId) {
        if (!searchRequest.isBlank()) {
            userService.checkUserExistence(userId);
            return itemRepository.findItem(searchRequest.trim())
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return new LinkedList<>();
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long ownerId) {
        Item updatingItem = findItemById(itemId);
        if (ownerId == updatingItem.getOwner().getId()) {
            if (itemDto.getName() != null) {
                updatingItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                updatingItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                updatingItem.setAvailable(itemDto.getAvailable());
            }
        } else {
            throw new IllegalArgumentException("Редактировать информацию о вещи может только её владелец");
        }
        return ItemMapper.toItemDto(updatingItem);
    }

    @Transactional
    @Override
    public void deleteItemById(long itemId, long userId) {
        userService.checkUserExistence(userId);
        Item item = findItemById(itemId);
        if (item.getOwner().getId() == userId) {
            if (item.getAvailable()) {
                itemRepository.deleteById(itemId);
            }
        } else {
            throw new IllegalArgumentException("Удалить вещь может только её владелец");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Item findItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Вещи с ID=%d не существует", itemId)));
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, long itemId, long commentatorId) {
        User commentator = userService.findUserById(commentatorId);
        Item item = findItemById(itemId);
        bookingRepository.findByBookerIdAndItemIdAndEndBefore(commentatorId, itemId, LocalDateTime.now())
                .stream()
                .findAny()
                .orElseThrow(() -> new InvalidArgumentException(String.format("Пользователь с ID=%d не может "
                        + "комментировать вещь с ID=%d, которую ранее не арендовал", commentatorId, itemId)));
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(commentator)
                .created(LocalDateTime.now())
                .build();
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private BookingInfoDto getLastBookingInfoDto(long itemId) {
        findItemById(itemId);
        List<Booking> bookings = bookingRepository.findByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
        return !bookings.isEmpty() ? BookingMapper.toBookingInfoDto(bookings.get(0)) : null;
    }

    private BookingInfoDto getNextBookingInfoDto(long itemId) {
        findItemById(itemId);
        List<Booking> bookings = bookingRepository.findByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
        return !bookings.isEmpty() ? BookingMapper.toBookingInfoDto(bookings.get(0)) : null;
    }
}