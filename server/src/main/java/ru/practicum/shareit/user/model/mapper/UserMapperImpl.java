package ru.practicum.shareit.user.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto mapTo(User entity) {
        return new UserDto(entity.getId(), entity.getEmail(), entity.getName());
    }

    @Override
    public User mapFrom(UserDto entity) {
        return new User(entity.getId(), entity.getEmail(), entity.getName());
    }

    @Override
    public User mapFrom(UserDto userDto, User user1) {
        if (userDto.getEmail() != null) {
            user1.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user1.setName(userDto.getName());
        }
        if (userDto.getEmail() == null && userDto.getName() == null) {
            throw new ConflictException("Передан пустой объект");
        }
        return user1;
    }
}
