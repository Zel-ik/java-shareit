package ru.practicum.shareit.users.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    @Test
    void toUserDto() {
        User user = new User(1L, "User name", "user@mail.com");
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