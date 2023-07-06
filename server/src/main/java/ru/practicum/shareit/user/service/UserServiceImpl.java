package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user");
        User user = userMapper.mapFrom(userDto);
        return userMapper.mapTo(userRepository.save(user));
    }

    @Override
    public UserDto getUser(long userId) {
        log.info("Getting user with id {}", userId);
        return userMapper.mapTo(userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с идентификатором %d не найден", userId))));
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Getting all users");
        return userRepository.findAll().stream().map(userMapper::mapTo).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("Update user with id {}", userId);
        userDto.setId(userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с идентификатором %d не найден", userId)));

        return userMapper.mapTo(userMapper.mapFrom(userDto, user));
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("Deleted user with id {}", userId);
    }
}
