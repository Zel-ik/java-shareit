package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    @Test
    void toUserDto() {
        User user = new User(1, "Sasha", "Sasha@mail.ru");
        ItemRequest request = new ItemRequest();
        Item item = new Item(1, "Дрель", "Самая лучшая дрель в мире", true, user, request);
        ItemDto itemDto = ItemMapper.toDto(item);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getRequest(), item.getRequest());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void toItem() {
        ItemRequest request = new ItemRequest();
        User user = new User(1, "Sasha", "Sasha@mail.ru");
        ItemDto itemDto = new ItemDto(1, "Дрель", "Самая лучшая дрель в мире", true, request);
        Item item = ItemMapper.toItem(itemDto, user);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getRequest(), item.getRequest());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }
}
