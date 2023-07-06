package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constans.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void creteModel() {

    }

    private static final User myUser = User.builder()
            .id(1L)
            .email("artem@yandex.com")
            .name("Artem")
            .build();

    private static final User otherUser = User.builder()
            .id(2L)
            .email("mark@gmail.com")
            .name("Mark")
            .build();

    private static final Item myItem = Item.builder()
            .id(1L)
            .name("Item")
            .owner(myUser)
            .description("Description item")
            .available(true)
            .build();

    private static final Comment comment = Comment.builder()
            .id(1L)
            .item(myItem)
            .author(otherUser)
            .text("Cool!")
            .build();

    private static final CommentOutDto commentOutDto = CommentOutDto.builder()
            .text("Cool!")
            .build();

    private static final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now())
            .item(myItem)
            .booker(otherUser)
            .build();

    @Test
    void addItem() {
        when(userRepository.findById(myUser.getId()))
                .thenReturn(Optional.of(myUser));
        when(itemRepository.save(myItem))
                .thenReturn(myItem);
        ItemDto itemDto = itemService.addItem(ItemMapper.toItemDto(myItem), myUser.getId());
        assertEquals(itemDto, ItemMapper.toItemDto(myItem));
        verify(itemRepository).save(myItem);
    }

    @Test
    void addItemWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.addItem(ItemMapper.toItemDto(myItem), 100L));
        assertEquals("Пользователя не существует", exception.getMessage());
    }

    @Test
    void updateItemWrongItem() {
        ItemDto itemUp = ItemDto.builder()
                .name("Updated name")
                .available(false)
                .build();
        when(userRepository.findById(myUser.getId()))
                .thenReturn(Optional.of(myUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.updateItem(itemUp, 1L, myUser.getId()));
        assertEquals("Предмета не существует", exception.getMessage());
    }

    @Test
    void updateItemWrongUser() {
        ItemDto itemUp = ItemDto.builder()
                .name("Updated name")
                .available(false)
                .build();
        when(userRepository.findById(myUser.getId()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.updateItem(itemUp, 1L, myUser.getId()));
        assertEquals("Пользователя не существует", exception.getMessage());
    }

    @Test
    void updateItemReturn() {
        ItemDto itemUp = ItemDto.builder()
                .name("Updated name")
                .available(false)
                .build();
        when(userRepository.findById(myUser.getId()))
                .thenReturn(Optional.of(myUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(myItem));
        when(itemRepository.save(any(Item.class))).then(AdditionalAnswers.returnsFirstArg());
        ItemDto itemUpdate = itemService.updateItem(itemUp, 1L, myUser.getId());
        assertThat(itemUpdate.getId()).isEqualTo(1);
        assertThat(itemUpdate.getName()).isEqualTo("Updated name");
        assertThat(itemUpdate.getDescription()).isEqualTo("Description item");
        assertThat(itemUpdate.getAvailable()).isFalse();
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(myItem.getId())).thenReturn(Optional.of(myItem));
        when(bookingRepository
                .findAllByItem_IdAndItem_Owner_IdAndStatusNotOrderByStartAsc(
                        myItem.getId(), myUser.getId(), Status.REJECTED
                )).thenReturn(List.of(booking));
        when(commentRepository.findAllByItem_IdOrderByCreated(myItem.getId()))
                .thenReturn(List.of(comment));

        ItemBookingDto itemBookingDto = itemService.getItemById(myItem.getId(), myUser.getId());
        CommentDto commentDto = CommentMapper.toDto(comment);

        assertEquals(itemBookingDto, ItemMapper.toItemBookingDto(myItem, List.of(commentDto), booking, null));
    }

    @Test
    void getItemByIdWithoutItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.getItemById(133L, myUser.getId()));
        assertEquals("Предмета не существует", exception.getMessage());
    }

    @Test
    void getAllItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(myUser));
        when(itemRepository.findAllByOwnerIdOrderById(myItem.getId())).thenReturn((List.of(myItem)));

        Collection<ItemBookingDto> result = itemService.getAllItem(myUser.getId());
        assertEquals(1, result.size());
    }

    @Test
    void searchItemInNormalCase() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(myUser));
        when(itemRepository.search(anyString()))
                .thenReturn(List.of(myItem));

        Collection<ItemDto> itemDto = itemService.searchItem("Description", myUser.getId());
        assertEquals(1, itemDto.size());
        assertEquals("Item", itemDto.stream().collect(Collectors.toList()).get(0).getName());
    }

    @Test
    void searchItemInRandomCase() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(myUser));
        when(itemRepository.search(anyString()))
                .thenReturn(List.of(myItem));

        Collection<ItemDto> itemDto = itemService.searchItem("deSCRipTioN", myUser.getId());
        assertEquals(1, itemDto.size());
        assertEquals("Item", itemDto.stream().collect(Collectors.toList()).get(0).getName());
    }

    @Test
    void searchItemInRandomCaseName() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(myUser));
        when(itemRepository.search(anyString()))
                .thenReturn(List.of(myItem));

        Collection<ItemDto> itemDto = itemService.searchItem("iTEM", myUser.getId());
        assertEquals(1, itemDto.size());
        assertEquals("Item", itemDto.stream().collect(Collectors.toList()).get(0).getName());
    }

    @Test
    void addComment() {
        when(bookingRepository.findAllByItem_IdAndBooker_IdAndStatusNotAndStartBefore(anyLong(), anyLong(), any(Status.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(myItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(otherUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDto = itemService
                .addComment(otherUser.getId(), myItem.getId(), CommentMapper.toCommentOutDto(comment));
        assertEquals("Cool!", commentDto.getText());
        assertEquals("Mark", commentDto.getAuthorName());
    }

    @Test
    void addCommentNullAuthor() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.addComment(otherUser.getId(), myItem.getId(), new CommentOutDto("Hi")));
        assertEquals("Пользователя не существует", exception.getMessage());
    }
}