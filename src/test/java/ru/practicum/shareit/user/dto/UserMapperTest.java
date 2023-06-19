package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    @Test
    void toUserDto() {
        User user = new User(1, "User name", "user@mail.com");
        UserDto userDto = UserMapper.toUserDto(user);
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void toUser() {
        UserDto userDto = new UserDto(1L, "User name", "user@mail.com");
        User user = UserMapper.toUser(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), user.getName());
    }
}
