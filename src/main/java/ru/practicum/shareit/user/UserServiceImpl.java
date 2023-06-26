package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(findUserById(userId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User updatingUser = findUserById(userId);
        if (userDto.getEmail() != null) {
            if (!updatingUser.getEmail().equals(userDto.getEmail())) {
                updatingUser.setEmail(userDto.getEmail());
            }
        }
        if (userDto.getName() != null) {
            updatingUser.setName(userDto.getName());
        }
        return UserMapper.toUserDto(updatingUser);
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        findUserById(userId);
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public void checkUserExistence(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(String.format("Пользователя с ID=%d не существует", userId));
        }
    }

    @Transactional(readOnly = true)
    public User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователя с ID=%d не существует", userId)));
    }
}