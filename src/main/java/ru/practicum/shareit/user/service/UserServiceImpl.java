package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого пользователя")));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto user, long userId) {
        if (!getUserById(userId).getEmail().equals(user.getEmail())) {
            checkEmail(user);
        }
        User updateUser = UserMapper.toUser(getUserById(userId));
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(updateUser));
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    private Boolean checkEmail(UserDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Такой Email уже существует");
        } else {
            return true;
        }
    }
}