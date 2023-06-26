package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constans.Status;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemServiceImplIntegrationsTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getAllItems() {
        User itemOwner = createUser("1");
        User anotherUser = createUser("2");
        User commentAuthor = createUser("3");
        userRepository.saveAll(List.of(itemOwner, anotherUser, commentAuthor));
        Long itemOwnerId = itemOwner.getId();
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", anotherUser);
        Item item3 = createItem("3", itemOwner);
        itemRepository.saveAll(List.of(item1, item2, item3));
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item1)
                .booker(anotherUser)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        Comment comment1ForItem3 = Comment.builder().text("Comment1").item(item3).author(commentAuthor).created(LocalDateTime.now()).build();
        Comment comment2ForItem3 = Comment.builder().text("Comment2").item(item3).author(commentAuthor).created(LocalDateTime.now()).build();
        commentRepository.saveAll(List.of(comment1ForItem3, comment2ForItem3));

        List<ItemBookingDto> allItems = (List<ItemBookingDto>) itemService.getAllItem(itemOwnerId);

        assertThat(allItems.size()).isEqualTo(2);
        assertThat(allItems.get(0).getName()).isEqualTo("Item1");
        assertThat(allItems.get(0).getLastBooking()).isNotNull();
        assertThat(allItems.get(1).getName()).isEqualTo("Item3");
        assertThat(allItems.get(1).getComments().size()).isEqualTo(2);
        assertThat(allItems.get(1).getComments().get(0).getText()).isEqualTo("Comment1");
        assertThat(allItems.get(1).getComments().get(1).getText()).isEqualTo("Comment2");
    }

    User createUser(String userPostfix) {
        return User.builder()
                .name(String.format("Name%s", userPostfix))
                .email(String.format("email@email%s.ru", userPostfix))
                .build();
    }

    Item createItem(String itemPostfix, User itemOwner) {
        return Item.builder()
                .name(String.format("Item%s", itemPostfix))
                .description(String.format("ItemDescription%s", itemPostfix))
                .available(true)
                .owner(itemOwner).build();
    }
}