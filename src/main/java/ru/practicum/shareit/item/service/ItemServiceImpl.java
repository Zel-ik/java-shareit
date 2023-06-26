package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constans.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        checkUser(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя не существует"));
        Item item = ItemMapper.toItem(itemDto, user);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto((savedItem));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        checkUser(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя не существует"));
        Item item = ItemMapper.toItem(itemDto, user);
        Item updateItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмета не существует"));
        if (item.getOwner() != null) {
            updateItem.setOwner(item.getOwner());
        }
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(updateItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBookingDto getItemById(long id, long userId) {
        checkUser(userId);
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмета не существует"));
        Collection<Booking> bookings = bookingRepository.findAllByItem_IdAndItem_Owner_IdAndStatusNotOrderByStartAsc(id, userId, Status.REJECTED);
        List<CommentDto> comments = commentRepository
                .findAllByItem_IdOrderByCreated(id)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        return ItemMapper.toItemBookingDto(item, comments, previousBookingInSorted(bookings), nextBookingInSorted(bookings));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemBookingDto> getAllItem(long userId) {
        checkUser(userId);
        Collection<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        Set<Long> itemsIds = items.stream().map(Item::getId).collect(Collectors.toSet());
        Map<Long, List<Comment>> comments = commentRepository
                .findAllByItem_IdInOrderByItem_Id(itemsIds)
                .stream()
                .collect(Collectors.groupingBy(it -> it.getItem().getId()));
        Map<Long, List<Booking>> itemIdToBookings = bookingRepository
                .findAllByItem_IdInOrderByStartAsc(itemsIds)
                .stream()
                .collect(Collectors.groupingBy(it -> it.getItem().getId()));

        List<ItemBookingDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(
                    item,
                    comments.getOrDefault(item.getId(), List.of()).stream().map(CommentMapper::toDto).collect(Collectors.toList()),
                    previousBookingInSorted(itemIdToBookings.getOrDefault(item.getId(), List.of())),
                    nextBookingInSorted(itemIdToBookings.getOrDefault(item.getId(), List.of()))
            );
            result.add(itemBookingDto);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> searchItem(String text, long userId) {
        checkUser(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentOutDto commentOutDto) {
        checkUser(userId);
        if (commentOutDto.getText().isEmpty()) {
            throw new ValidationException("Комментарий пустой");
        }
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя не существует"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмета не существует"));
        Collection<Booking> bookingList = bookingRepository
                .findAllByItem_IdAndBooker_IdAndStatusNotAndStartBefore(itemId, userId, Status.REJECTED, LocalDateTime.now());
        if (bookingList.isEmpty())
            throw new ValidationException("Пользователь не брал данный прибор и не может оставить комментарий");
        Comment comment = Comment.builder()
                .text(commentOutDto.getText())
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private Booking previousBookingInSorted(Collection<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        Booking prev = null;
        Booking cur = null;
        for (Booking booking : bookings) {
            cur = booking;
            if (prev != null && (cur.getStart().isAfter(now))) {
                return prev;
            }
            prev = cur;
        }
        return cur;
    }

    private Booking nextBookingInSorted(Collection<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        for (Booking cur : bookings) {
            if (cur.getStart().isAfter(now)) {
                return cur;
            }
        }
        return null;
    }

    private void checkUser(Long userId) {
        if (userId == null) {
            throw new NotFoundException("Пользователь не указан");
        } else if (userRepository.findById(userId) == null) {
            throw new NotFoundException("Указанный пользователь не сущетсвует");
        }
    }
}