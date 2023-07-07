package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @DirtiesContext
    @Test
    void searchItem() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");

        Item itemByDescription = new Item();
        itemByDescription.setName("name");
        itemByDescription.setDescription("test");
        itemByDescription.setAvailable(true);
        itemByDescription.setOwner(user);

        Item itemByName = new Item();
        itemByName.setName("test");
        itemByName.setDescription("description");
        itemByName.setAvailable(true);
        itemByName.setOwner(user);

        PageRequest pageable = PageRequest.of(0, 10);

        entityManager.persist(user);
        entityManager.persist(itemByDescription);
        entityManager.persist(itemByName);

        List<Item> result = repository.searchItem("test", pageable);
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertThat(result).contains(itemByDescription),
                () -> assertThat(result).contains(itemByName));
    }
}
