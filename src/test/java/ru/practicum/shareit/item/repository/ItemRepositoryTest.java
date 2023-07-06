package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final TestEntityManager entityManager;

    @Test
    void findAvailableItemsByText() {
        User owner = User.builder().name("Name").email("tema@gmail.com").build();
        entityManager.persist(owner);
        Item item1 = Item.builder()
                .name("Ручка")
                .description("description1")
                .available(true)
                .owner(owner)
                .build();
        Item item2 = Item.builder()
                .name("2 ручка")
                .description("description2")
                .available(true)
                .owner(owner)
                .build();
        Item item3 = Item.builder()
                .name("Name")
                .description("руЧКА")
                .available(true)
                .owner(owner)
                .build();
        Item item4 = Item.builder()
                .name("item")
                .description("something")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.persist(item4);

        List<Item> items = itemRepository.search("ручка");

        assertThat(items.size()).isEqualTo(3);
        assertThat(items).containsExactly(item1, item2, item3);
    }
}