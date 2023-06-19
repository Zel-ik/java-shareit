package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUserById(long id);

    void checkUserExistence(long userId);

    User findUserById(long userId);
}