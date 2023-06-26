package ru.practicum.shareit.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController controller;

    @Mock
    private UserService userService;

    private MockMvc mvc;

    private UserDto user;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        user = UserDto.builder()
                .id(1L)
                .email("artem@yandex.com")
                .name("Artem")
                .build();
    }

    @Test
    void createUser() throws Exception {
        when(userService.saveUser(any(UserDto.class)))
                .thenReturn(user);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userUp = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .build();
        when(userService.updateUser(any(UserDto.class), anyLong()))
                .thenReturn(userUp);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userUp))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userUp.getId()))
                .andExpect(jsonPath("$.email").value(userUp.getEmail()))
                .andExpect(jsonPath("$.name").value(userUp.getName()));
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> allUsers = List.of(
                UserDto.builder()
                        .id(2L)
                        .email("ivan@yandex.com")
                        .name("Ivan")
                        .build(),
                UserDto.builder()
                        .id(2L)
                        .email("Olga@yandex.com")
                        .name("Olga")
                        .build()
        );
        when(userService.getAllUsers())
                .thenReturn(allUsers);
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(1L);
    }
}