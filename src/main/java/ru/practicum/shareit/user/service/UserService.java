package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto saveUser(UserDto user);

    UserDto getUserById(long userId);

    UserDto updateUser(UserDto user, long userId);

    void deleteUser(long userId);
}