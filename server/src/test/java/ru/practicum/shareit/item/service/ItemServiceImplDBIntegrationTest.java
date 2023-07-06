package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ItemServiceImplDBIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void testGetItems() {
        User user = new User();
        user.setEmail("test_user@mail.ru");
        user.setName("password");
        user = userRepository.save(user);
        Long userId = userRepository.findByEmail("test_user@mail.ru").get().getId();

        Item item = new Item();
        item.setUser(user);
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);
        item.setComments(new ArrayList<>());
        itemRepository.save(item);

        List<ItemDto> items = itemService.getItems(userId);
        assertNotNull(items);
        assertTrue(items.size() > 0);
        ItemDto itemDto = items.get(0);
        assertNotNull(itemDto.getName());
        assertNotNull(itemDto.getDescription());
        assertNotNull(itemDto.getAvailable());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        assertEquals(itemDto.getComments().size(), 0);
        assertNull(itemDto.getRequestId());
    }
}