package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;

    Item item1test;
    Item item2test;
    Item item3test;
    Item item4test;


    @BeforeEach
    void createUsersAndItems() {
        User user2 = new User(2, "user", "user@mail.ru");
        User user3 = new User(3, "user3", "user3@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        Item item1 = new Item(0, "item1", "item1description", true, user2, itemRequest);
        item1test = itemService.addItem(item1);
        Item item2 = new Item(0, "item2", "item2description", true, user2, itemRequest);
        item2test = itemService.addItem(item2);
        Item item3 = new Item(
                0, "item3", "itemSearchingDescription", true, user2, itemRequest);
        item3test = itemService.addItem(item3);
        Item item4 = new Item(
                0, "item4", "itemSearchingDescription", false, user3, itemRequest);
        item4test = itemService.addItem(item4);
    }

    @AfterEach
    void clear() {
        itemRepository.deleteItem(1);
        itemRepository.deleteItem(2);
        itemRepository.deleteItem(3);
        itemRepository.deleteItem(4);
        itemRepository.makeGeneratorToOne();

    }

    @Test
    void createItem() {
        User user2 = new User(2, "user", "user@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        Item item = new Item("item1", "item1description");
        item.setId(1);
        item.setAvailable(true);
        item.setOwner(user2);
        item.setRequest(itemRequest);
        Assertions.assertThat(item).isEqualTo(item1test);
    }

    @Test
    void getItem() {
        Item item = itemService.getItemById(1);
        Assertions.assertThat(item).isEqualTo(item1test);
    }

    @Test
    void shouldReturn3WhenGetAllListSize() {
        Assertions.assertThat(itemService.getAllItem(2).size()).isEqualTo(3);
    }

    @Test
    void updateItemWithAllFields() {
        User user2 = new User(2, "user", "user@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        Item itemUpdate = new Item(
                1, "itemUpdate", "itemUpdateDescription", false, user2, itemRequest);
        Item item = itemService.updateItem(itemUpdate, 1);
        Assertions.assertThat(item).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "itemUpdate")
                .hasFieldOrPropertyWithValue("description", "itemUpdateDescription")
                .hasFieldOrPropertyWithValue("available", false);
    }

    @Test
    void updateItemWithNameFieldOnly() {
        User user2 = new User(2, "user", "user@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        Item itemUpdate = new Item(1, "itemUpdate", null, null, user2, itemRequest);
        Item item = itemService.updateItem(itemUpdate, 1);
        Assertions.assertThat(item).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "itemUpdate")
                .hasFieldOrPropertyWithValue("description", "item1description")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void updateItemWithDescriptionFieldOnly() {
        ItemRequest itemRequest = new ItemRequest();
        User user2 = new User(2, "user", "user@mail.ru");
        Item itemUpdate = new Item(1, null, "itemUpdateDescription", null, user2, itemRequest);
        Item item = itemService.updateItem(itemUpdate, 1);
        Assertions.assertThat(item).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item1")
                .hasFieldOrPropertyWithValue("description", "itemUpdateDescription")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void updateItemWithAvailableFieldOnly() {
        ItemRequest itemRequest = new ItemRequest();
        User user2 = new User(2, "user", "user@mail.ru");
        Item itemUpdate = new Item(1, null, null, false, user2, itemRequest);
        Item item = itemService.updateItem(itemUpdate, 1);
        Assertions.assertThat(item).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "item1")
                .hasFieldOrPropertyWithValue("description", "item1description")
                .hasFieldOrPropertyWithValue("available", false);
    }

    @Test
    void shouldReturn1WhenSearchListSize() {
        Assertions.assertThat(itemService.searchItem("SearCHing")).size().isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyListWhenSearchFindsNothing() {
        Assertions.assertThat(itemService.searchItem("AAAAaadff")).size().isEqualTo(0);
    }
}
