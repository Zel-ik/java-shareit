package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceImplDBIntegrationTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testGetUsers() {
        userRepository.deleteAll();
        User user1 = new User(null, "user1@example.com", "User 1");
        User user2 = new User(null, "user2@example.com", "User 2");
        userRepository.saveAll(Arrays.asList(user1, user2));

        List<UserDto> users = userService.getUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getEmail()).isEqualTo("user1@example.com");
        assertThat(users.get(0).getName()).isEqualTo("User 1");
        assertThat(users.get(1).getEmail()).isEqualTo("user2@example.com");
        assertThat(users.get(1).getName()).isEqualTo("User 2");
    }
}