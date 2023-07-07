package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("test");
        userDto.setEmail("test@test.ru");
    }

    @Test
    void createUser() {
        UserDto createdUser = userService.createUser(userDto);
        UserDto readUser = userService.getUserById(createdUser.getId());
        assertEquals(readUser, createdUser);
    }

    @Test
    void createUserWithNotUniqueEmail() {
        userService.createUser(userDto);
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> userService.createUser(userDto));
        assertEquals("Пользователь с email=test@test.ru уже существует.", ex.getMessage());
    }

    @Test
    void createUserWithNoEmail() {
        ValidationException ex = assertThrows(ValidationException.class, () -> userService.createUser(new UserDto()));
        assertEquals("Невозможно создать пользователя без email", ex.getMessage());
    }

    @Test
    void updateUser() {
        UserDto createdUser = userService.createUser(userDto);
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userService.updateUser(createdUser.getId(), newUserDto);
        UserDto updatedUserDto = userService.getUserById(createdUser.getId());
        assertEquals("Test2", updatedUserDto.getName(), "Имя не совпадает.");
        assertEquals("newtest@test.ru", updatedUserDto.getEmail(), "Email не совпадает.");
    }

    @Test
    void updateUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> userService.updateUser(100L, userDto));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void updateUserWithNotUniqueEmail() {
        UserDto createdUser = userService.createUser(userDto);
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        userService.createUser(newUserDto);
        EntityAlreadyExistException ex = assertThrows(EntityAlreadyExistException.class, () -> userService.updateUser(createdUser.getId(), userDto));
        assertEquals("Пользователь с email=newtest@test.ru уже существует.", ex.getMessage());
    }

    @Test
    void deleteUser() {
        UserDto createdUser = userService.createUser(userDto);
        userService.deleteUser(createdUser.getId());
        assertThrows(EntityNotExistException.class, () -> userService.getUserById(createdUser.getId()));
    }

    @Test
    void deleteUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> userService.deleteUser(100L));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void getUsers() {
        UserDto createdUser = userService.createUser(userDto);
        assertEquals(userService.getUsers(), List.of(createdUser));
    }
}
