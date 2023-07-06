package ru.practicum.shareit.user.model.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperImplTest {

    @Mock
    private User user;

    @Mock
    private UserDto userDto;

    @InjectMocks
    private UserMapperImpl mapper;

    @Test
    void mapTo_shouldReturnUserDto() {
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getName()).thenReturn("John Doe");

        UserDto result = mapper.mapTo(user);

        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void mapFrom_shouldReturnUser() {
        when(userDto.getId()).thenReturn(1L);
        when(userDto.getEmail()).thenReturn("test@example.com");
        when(userDto.getName()).thenReturn("John Doe");

        User result = mapper.mapFrom(userDto);

        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void mapFrom_shouldUpdateUser() {
        when(userDto.getEmail()).thenReturn("test@example.com");
        when(userDto.getName()).thenReturn("John Doe");

        User result = mapper.mapFrom(userDto, user);

        verify(user).setEmail("test@example.com");
        verify(user).setName("John Doe");
        assertEquals(user, result);
    }

    @Test
    void mapFrom_shouldThrowConflictException_whenUserDtoIsEmpty() {
        when(userDto.getEmail()).thenReturn(null);
        when(userDto.getName()).thenReturn(null);

        assertThrows(ConflictException.class, () -> mapper.mapFrom(userDto, user));
    }
}