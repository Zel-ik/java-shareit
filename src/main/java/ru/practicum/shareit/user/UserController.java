package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validationgroup.Create;
import ru.practicum.shareit.validationgroup.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        UserDto newUserDto = userService.createUser(userDto);
        log.info("Создан пользователь: {}", newUserDto);
        return newUserDto;
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable long userId) {
        UserDto userDto = userService.getUserById(userId);
        log.info("Запрошен пользователь с ID={} {}", userId, userDto);
        return userDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable long userId,
                              @RequestBody @Validated(Update.class) UserDto userDto) {
        UserDto updatedUserDto = userService.updateUser(userId, userDto);
        log.info("Данные пользователя c ID={} обновлены: {}", userId, updatedUserDto);
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable long userId) {
        userService.deleteUserById(userId);
        log.info("Пользователь с ID={} удалён", userId);
    }
}