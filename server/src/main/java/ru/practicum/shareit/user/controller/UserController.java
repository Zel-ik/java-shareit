package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto user) {
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }
}
