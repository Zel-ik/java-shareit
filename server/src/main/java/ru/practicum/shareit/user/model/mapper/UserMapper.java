package ru.practicum.shareit.user.model.mapper;

import ru.practicum.shareit.entityMapper.EntityMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

public interface UserMapper extends EntityMapper<UserDto, User> {
    User mapFrom(UserDto userDto, User user);
}
