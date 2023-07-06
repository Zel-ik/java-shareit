package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private ItemRequest request;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        entityManager.persist(user);

        Item item1 = new Item(null, user, "Item 1", "Description 1", true, new ArrayList<>());
        Item item2 = new Item(null, user, "Item 2", "Description 2", false,
                Collections.emptyList());
        Item item3 = new Item(null, user, "Another Item", "This item is also available", true,
                Collections.emptyList());

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);

        request = new ItemRequest(null, "Please give me this item", LocalDateTime.now(), user,
                null);
        entityManager.persist(request);

        item1.setItemRequest(request);
        item2.setItemRequest(request);

        entityManager.flush();
    }

    @AfterEach
    public void tearDown() {
        entityManager.clear();
    }

    @Test
    void testFindAllByUserId() {
        List<Item> items = itemRepository.findAllByUserId(user.getId());
        assertEquals(3, items.size());
    }

    @Test
    void testItemsByText() {
        List<Item> items = itemRepository.itemsByText("Item");
        assertEquals(3, items.size());
    }

    @Test
    void testFindByItemRequestIdAndAvailableTrue() {
        List<Item> items = itemRepository.findByItemRequestIdAndAvailableTrue(request.getId());
        assertEquals(1, items.size());
    }

}