package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final UserService userService;
    private final UserRepository userRepository;
    User user1;
    User user2;


    @BeforeEach
    void createUsers() {
        User testUser = new User(0,"user1", "user1@mail.ru");
        user1 = userService.saveUser(testUser);
        User testUser2 = new User(0,"user2", "user2@mail.ru");
        user2 = userService.saveUser(testUser2);
    }

    @AfterEach
    void clear() {
        userRepository.deleteUser(1);
        userRepository.deleteUser(2);
        userRepository.makeIdCounterToOne();
    }

    @Test
    void createUser() {
        Assertions.assertThat(user1).hasFieldOrPropertyWithValue("name", "user1")
                .hasFieldOrPropertyWithValue("email", "user1@mail.ru")
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void updateUser() {
        User updateUser = new User(1, "userUpdateName", "userUpdate@mail.ru");
        user1 = userService.updateUser(updateUser, 1);
        Assertions.assertThat(user1).hasFieldOrPropertyWithValue("name", "userUpdateName")
                .hasFieldOrPropertyWithValue("email", "userUpdate@mail.ru")
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void getUser() {
        Assertions.assertThat(userService.getUserById(1)).hasFieldOrPropertyWithValue("name", "user1")
                .hasFieldOrPropertyWithValue("email", "user1@mail.ru")
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void shouldReturnNullAfterDeleteUser() {
        userService.deleteUser(1);
        Assertions.assertThat(userService.getAllUsers().size()).isEqualTo(1);
    }

    @Test
    void shouldReturn2WhenFindAll() {
        Collection<User> users = userService.getAllUsers();
        Assertions.assertThat(users.size()).isEqualTo(2);
    }

}
