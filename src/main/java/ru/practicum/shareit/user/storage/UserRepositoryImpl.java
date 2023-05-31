package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public void makeIdCounterToOne(){
        idCounter = 1;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User saveUser(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public User updateUser(User user, long userId) {
        user.setId(userId);
        if (user.getEmail() == null) {
            user.setEmail(getUserById(userId).getEmail());
        } else if (user.getName() == null) {
            user.setName(getUserById(userId).getName());
        }
        users.put(userId, user);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }
}
