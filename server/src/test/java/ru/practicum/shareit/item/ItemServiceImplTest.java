package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService requestService;
    private final BookingService bookingService;
    private ItemDto itemDto;
    private UserDto userDto;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("test");
        userDto.setEmail("test@test.ru");

        itemDto = new ItemBookingDto();
        itemDto.setId(1L);
        itemDto.setName("test");
        itemDto.setDescription("test description");
        itemDto.setAvailable(true);

        requestDto = new ItemRequestDto();
        requestDto.setDescription("test");
    }

    @Test
    void createItem() {
        UserDto createdUser = userService.createUser(userDto);
        ItemRequestDto createdRequest = requestService.createRequest(createdUser.getId(), requestDto);
        itemDto.setRequestId(createdRequest.getId());
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        ItemDto readItemDto = itemService.getItemById(createdUser.getId(), createdItemDto.getId());
        assertEquals("test", readItemDto.getName(), "Имя вещи не совпадает.");
        assertEquals("test description", readItemDto.getDescription(), "Описание вещи не совпадает.");
        assertTrue(readItemDto.getAvailable(), "Вещь недоступна.");
    }

    @Test
    void createItemUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.createItem(100L, itemDto));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void createItemRequestNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        itemDto.setRequestId(100L);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.createItem(createdUser.getId(), itemDto));
        assertEquals("Запрос с id=100 не существует.", ex.getMessage());
    }

    @Test
    void updateItem() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        ItemDto newItemDto = new ItemBookingDto();
        newItemDto.setName("test2");
        newItemDto.setDescription("test2 description");
        newItemDto.setAvailable(true);
        ItemDto updatedItemDto = itemService.updateItem(createdUser.getId(), createdItemDto.getId(), newItemDto);

        assertEquals("test2", updatedItemDto.getName(), "Имя вещи не совпадает.");
        assertEquals("test2 description", updatedItemDto.getDescription(), "Описание вещи не совпадает.");
    }

    @Test
    void updateItemNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.updateItem(createdUser.getId(),100L, itemDto));
        assertEquals("Вещь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void updateItemUserNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.updateItem(100L,createdItemDto.getId(), itemDto));
        assertEquals("Пользователь с id=100 не владелец вещи.", ex.getMessage());
    }

    @Test
    void deleteItem() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        itemService.deleteItem(createdUser.getId(), createdItemDto.getId());
        assertThrows(EntityNotExistException.class, () -> itemService.getItemById(createdUser.getId(), createdItemDto.getId()));
    }

    @Test
    void deleteItemNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.deleteItem(createdUser.getId(),100L));
        assertEquals("Вещь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void deleteItemUserNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.deleteItem(100L,createdItemDto.getId()));
        assertEquals("Пользователь с id=100 не владелец вещи.", ex.getMessage());
    }

    @Test
    void getUserItems() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        List<ItemBookingDto> items = itemService.getUserItems(createdUser.getId(), 0, 10);

        assertEquals(createdItemDto.getDescription(), items.get(0).getDescription(), "Описание вещи не совпадает.");
        assertEquals(createdItemDto.getName(), items.get(0).getName(), "Имя вещи не совпадает.");
        assertEquals(1, items.size(), "Размер списка не совпадает.");
    }

    @Test
    void getItemById() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);

        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(createdItemDto.getId());
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 14, 0, 0));
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));
        BookingResponseDto lastBookingDto = bookingService.createBooking(otherUserDto.getId(), bookingDto);

        BookingDto otherBookingDto = new BookingDto();
        otherBookingDto.setItemId(createdItemDto.getId());
        otherBookingDto.setStart(LocalDateTime.now().plusDays(1));
        otherBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto nextBookingDto = bookingService.createBooking(otherUserDto.getId(), otherBookingDto);

        bookingService.approveBooking(createdUser.getId(), lastBookingDto.getId(), true);
        bookingService.approveBooking(createdUser.getId(), nextBookingDto.getId(), true);
        ItemBookingDto readItem = itemService.getItemById(createdUser.getId(), createdItemDto.getId());

        assertEquals(lastBookingDto.getId(), readItem.getLastBooking().getId(), "Id последнего бронирования не совпадает.");
        assertEquals(nextBookingDto.getId(), readItem.getNextBooking().getId(),"Id следующего бронирования не совпадает.");
    }

    @Test
    void getItemsUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.getUserItems(100L,0, 10));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void searchItem() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        List<ItemDto> items = itemService.searchItem("test", 0, 10);
        assertEquals(createdItemDto.getDescription(), items.get(0).getDescription(), "Описание не совпадает.");
        assertEquals(createdItemDto.getName(), items.get(0).getName(), "Имя не совпадает.");
        assertEquals(1, items.size(), "Размер списка не совпадает.");
    }

    @Test
    void searchItemEmptyText() {
        UserDto createdUser = userService.createUser(userDto);
        itemService.createItem(createdUser.getId(), itemDto);
        List<ItemDto> items = itemService.searchItem("", 0, 10);
        assertEquals(0, items.size());
    }

    @Test
    void createComment() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(createdItemDto.getId());
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 14, 0, 0));
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));
        bookingService.createBooking(otherUserDto.getId(), bookingDto);
        itemService.createComment(otherUserDto.getId(), createdItemDto.getId(), commentDto);
        ItemBookingDto readItemDto = itemService.getItemById(createdUser.getId(), createdItemDto.getId());
        assertEquals("comment", readItemDto.getComments().get(0).getText(), "Текст комментария не совпадает.");
    }

    @Test
    void createCommentUserNotExist() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.createComment(100L, 100L, commentDto));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void createCommentItemNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> itemService.createComment(createdUser.getId(), 100L, commentDto));
        assertEquals("Вещь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void createCommentNotBooking() {
        UserDto createdUser = userService.createUser(userDto);
        ItemDto createdItemDto = itemService.createItem(createdUser.getId(), itemDto);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.createComment(createdUser.getId(), createdItemDto.getId(), commentDto));
        assertEquals(String.format("Пользователь с id=%d не бронировал вещь с id=%d.", createdUser.getId(), createdItemDto.getId()), ex.getMessage());
    }
}
