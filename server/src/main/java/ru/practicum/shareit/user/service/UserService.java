package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(long userId);

    List<UserDto> getUsers();

    UserDto updateUser(UserDto userDto, long userId);

    void deleteUser(long userId);
}
