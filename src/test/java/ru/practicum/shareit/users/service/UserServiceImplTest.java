package ru.practicum.shareit.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private User myUser;
    private User otherUser;

    @BeforeEach
    void creteModel() {
        myUser = User.builder()
                .id(1L)
                .email("artem@yandex.com")
                .name("Artem")
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("mark@gmail.com")
                .name("Mark")
                .build();
    }

    @Test
    void saveUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(myUser);
        UserDto userDto = userService.saveUser(UserMapper.toUserDto(myUser));
        assertEquals(userDto, UserMapper.toUserDto(myUser));
        verify(userRepository).save(myUser);
    }

    @Test
    void updateUser() {
        UserDto userUp = UserDto.builder()
                .email("123@mail.ru")
                .name("Update")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(myUser));
        when(userRepository.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        UserDto userDto = userService.updateUser(userUp, myUser.getId());
        assertThat(userDto.getId()).isEqualTo(1);
        assertThat(userDto.getName()).isEqualTo("Update");
        assertThat(userDto.getEmail()).isEqualTo("123@mail.ru");
    }

    @Test
    void updateUserOldEmail() {
        User changeUser = User.builder()
                .id(3L)
                .email("222@gmail.com")
                .name("mark")
                .build();
        UserDto userUp = UserDto.builder()
                .email("mark@gmail.com")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(changeUser));
        when((userRepository.existsByEmail(anyString()))).thenReturn(true);
        assertThatThrownBy(() -> userService.updateUser(userUp, changeUser.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Такой Email уже существует");
    }

    @Test
    void getUserById() {
        when(userRepository.findById(myUser.getId())).thenReturn(Optional.of(myUser));
        UserDto userDto = userService.getUserById(myUser.getId());
        assertEquals(1, userDto.getId());
        assertEquals("Artem", userDto.getName());
        assertEquals("artem@yandex.com", userDto.getEmail());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(myUser, otherUser));
        Collection<UserDto> allUsers = userService.getAllUsers();
        assertEquals(2, allUsers.size());
    }
}