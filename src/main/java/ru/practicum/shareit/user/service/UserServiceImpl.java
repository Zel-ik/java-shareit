package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User saveUser(User user) {
        checkEmail(user);
        return userRepository.saveUser(user);
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public User updateUser(User user, long userId) {
        if (!getUserById(userId).getEmail().equals(user.getEmail())) {
            checkEmail(user);
        }
        return userRepository.updateUser(user, userId);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }

    private Boolean checkEmail(User user) {
        if (userRepository.getAllUsers().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            throw new ConflictException("Такой Email уже существует");
        } else {
            return true;
        }
    }
}
