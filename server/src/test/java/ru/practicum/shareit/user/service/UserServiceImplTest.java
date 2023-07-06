package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void createUserTest() {
        UserDto userDtoFrom = new UserDto(null, "user@example.com", "John Doe");
        User userAfter = new User(null, "user@example.com", "John Doe");
        User userBefore = new User(1L, "user@example.com", "John Doe");
        UserDto userDtoTo = new UserDto(1L, "user@example.com", "John Doe");

        when(userMapper.mapFrom(userDtoFrom)).thenReturn(userAfter);
        when(userRepository.save(userAfter)).thenReturn(userBefore);
        when(userMapper.mapTo(userBefore)).thenReturn(userDtoTo);

        UserDto createdUser = userServiceImpl.createUser(userDtoFrom);

        assertNotNull(createdUser.getId());
        assertEquals(userDtoFrom.getEmail(), createdUser.getEmail());
        assertEquals(userDtoFrom.getName(), createdUser.getName());

        verify(userRepository, times(1)).save(userAfter);
        verify(userMapper, times(1)).mapTo(userBefore);
    }

    @Test
    void getUserTest() {
        long userId = 1L;

        User user = new User(userId, "user@example.com", "John Doe");
        UserDto userDto = new UserDto(userId, "user@example.com", "John Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.mapTo(user)).thenReturn(userDto);

        UserDto foundUser = userServiceImpl.getUser(userId);

        assertEquals(userDto, foundUser);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).mapTo(user);
    }

    @Test
    void getUserNotFoundTest() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userServiceImpl.getUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).mapTo(ArgumentMatchers.any(User.class));
    }

    @Test
    void getUsersTest() {
        User user1 = new User(1L, "user1@example.com", "John Doe");
        User user2 = new User(2L, "user2@example.com", "Jane Smith");

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        UserDto userDto1 = new UserDto(1L, "user1@example.com", "John Doe");
        UserDto userDto2 = new UserDto(2L, "user2@example.com", "Jane Smith");

        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto1);
        userDtoList.add(userDto2);

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.mapTo(user1)).thenReturn(userDto1);
        when(userMapper.mapTo(user2)).thenReturn(userDto2);

        List<UserDto> foundUsers = userServiceImpl.getUsers();

        assertEquals(userDtoList, foundUsers);

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).mapTo(user1);
        verify(userMapper, times(1)).mapTo(user2);
    }

    @Test
    void updateUserTest() {
        long userId = 1L;

        UserDto userDtoFrom = new UserDto(null, "userUpdate@example.com", "John Doe");
        User userAfter = new User(1L, "user@example.com", "John Doe");
        UserDto userDtoTo = new UserDto(1L, "userUpdate@example.com", "John Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userAfter));
        when(userMapper.mapFrom(userDtoFrom, userAfter)).thenReturn(userAfter);
        when(userMapper.mapTo(userAfter)).thenReturn(userDtoTo);

        UserDto updatedUser = userServiceImpl.updateUser(userDtoFrom, userId);

        assertEquals(userDtoFrom, updatedUser);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).mapFrom(userDtoFrom, userAfter);
        verify(userMapper, times(1)).mapTo(userAfter);
    }

    @Test
    void updateUserNotFoundTest() {
        long userId = 1L;

        UserDto userDto = new UserDto(userId, "user@example.com", "John Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userServiceImpl.updateUser(userDto, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).mapFrom(ArgumentMatchers.any(UserDto.class), ArgumentMatchers.any(User.class));
        verify(userRepository, never()).save(ArgumentMatchers.any(User.class));
        verify(userMapper, never()).mapTo(ArgumentMatchers.any(User.class));
    }

    @Test
    void deleteUserTest() {
        long userId = 1L;

        userServiceImpl.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
        verify(userMapper, never()).mapTo(ArgumentMatchers.any(User.class));
    }
}