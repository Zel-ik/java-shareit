package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    public void makeIdCounterToOne();

    User saveUser(User user);

    User getUserById(long userId);

    User updateUser(User user, long userId);

    void deleteUser(long userId);

    Collection<User> getAllUsers();
}
