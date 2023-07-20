package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    private void setBookingDates(ItemBookingDto item) {
        List<Booking> lastBookings = bookingRepository.findItemLastBookings(item.getId(),
                LocalDateTime.now());
        if (!lastBookings.isEmpty()) {
            item.setLastBooking(BookingMapper.INSTANCE.bookingToBookingForItemDto(lastBookings.get(0)));
        }
        List<Booking> nextBookings = bookingRepository.findItemNextBookings(item.getId(),
                LocalDateTime.now());
        if (!nextBookings.isEmpty()) {
            item.setNextBooking(BookingMapper.INSTANCE.bookingToBookingForItemDto(nextBookings.get(0)));
        }
    }

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("createItem: {}, userId={}", itemDto, userId);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        Item item = ItemMapper.INSTANCE.itemDtoToItem(itemDto);
        item.setOwner(userRepository.findById(userId).get());
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> {
                log.warn("request with id={} not exist", requestId);
                throw new EntityNotExistException(String.format("Запрос с id=%d не существует.", requestId));
            });
            item.setRequest(itemRequest);
        }
        return ItemMapper.INSTANCE.itemToItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("updateItem: {}", itemDto);
        Item updateItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("item with id={} not exist", itemId);
            throw new EntityNotExistException(String.format("Вещь с id=%d не существует.", itemId));
        });
        if (!updateItem.getOwner().getId().equals(userId)) {
            log.warn("user with id={} is not owner of item with id={}", userId, itemId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не владелец вещи.", userId));
        }
        if (itemDto.getName() != null) {
            updateItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updateItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.INSTANCE.itemToItemDto(updateItem);
    }

    @Transactional
    @Override
    public void deleteItem(Long userId, Long itemId) {
        log.info("deleteItem: id = {}, userId = {}", itemId, userId);
        Item deleteItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("item with id={} not exist", itemId);
            throw new EntityNotExistException(String.format("Вещь с id=%d не существует.", itemId));
        });
        if (!deleteItem.getOwner().getId().equals(userId)) {
            log.warn("user with id={} is not owner of item with id={}", userId, itemId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не владелец вещи.", userId));
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemBookingDto getItemById(Long userId, Long itemId) {
        log.info("getItemById with id={}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("item with id={} not exist", itemId);
            throw new EntityNotExistException(String.format("Вещь с id=%d не существует.", itemId));
        });
        Long ownerId = item.getOwner().getId();
        ItemBookingDto itemBookingDto = ItemMapper.INSTANCE.itemToItemBookingDto(item);
        if (ownerId.equals(userId)) {
            setBookingDates(itemBookingDto);
        }
        List<CommentDto> commentsDto = commentRepository.findAllByItemIdOrderById(itemId).stream()
                .map(CommentMapper.INSTANCE::commentToCommentDto)
                .collect(Collectors.toList());
        itemBookingDto.setComments(commentsDto);
        return itemBookingDto;
    }

    @Override
    public List<ItemBookingDto> getUserItems(Long userId, Integer from, Integer size) {
        log.info("getUserItems by user with id={}", userId);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        PageRequest pageRequest = new CustomPageRequest(from, size);
        List<ItemBookingDto> itemBookingDtoList = itemRepository.findByOwnerId(userId, pageRequest).stream()
                .map(ItemMapper.INSTANCE::itemToItemBookingDto)
                .collect(Collectors.toList());
        itemBookingDtoList.forEach(this::setBookingDates);
        itemBookingDtoList.sort(Comparator.comparing(ItemBookingDto::getId));
        return itemBookingDtoList;
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        log.info("searchItem by text={}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        PageRequest pageRequest = new CustomPageRequest(from, size);
        return itemRepository.searchItem(text, pageRequest).stream()
                .map(ItemMapper.INSTANCE::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("createComment: {}, userId={}, itemId={}", commentDto, userId, itemId);
        User author = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("item with id={} not exist", itemId);
            throw new EntityNotExistException(String.format("Вещь с id=%d не существует.", itemId));
        });
        bookingRepository.findFirstByBookerAndItemIdAndEndBefore(author, itemId, LocalDateTime.now()).orElseThrow(() -> {
            log.warn("user with id={} hasn't booked item with id={}", userId, itemId);
            throw new ValidationException(String.format("Пользователь с id=%d не бронировал вещь с id=%d.", userId, itemId));
        });
        Comment comment = CommentMapper.INSTANCE.commentDtoToComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.INSTANCE.commentToCommentDto(commentRepository.save(comment));
    }
}
