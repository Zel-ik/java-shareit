package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final UserService userService;
    private final ItemRequestService requestService;
    private UserDto userDto;
    private ItemRequestDto requestDto;


    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("test");
        userDto.setEmail("test@test.ru");

        requestDto = new ItemRequestDto();
        requestDto.setDescription("test");
    }

    @Test
    void createRequest() {
        UserDto createdUser = userService.createUser(userDto);
        ItemRequestDto createdRequest = requestService.createRequest(createdUser.getId(), requestDto);
        ItemRequestDto readRequest = requestService.getRequestById(createdUser.getId(), createdRequest.getId());
        assertEquals("test", readRequest.getDescription(), "Описание не совпадает.");
    }

    @Test
    void createRequestUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> requestService.createRequest(100L, requestDto));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void getRequestsByUser() {
        UserDto createdUser = userService.createUser(userDto);
        requestService.createRequest(createdUser.getId(), requestDto);
        List<ItemRequestDto> requests = requestService.getRequestsByUser(createdUser.getId());
        assertEquals(1, requests.size(), "Размер списка не совпадает.");
        assertEquals("test", requests.get(0).getDescription(), "Описание не совпадает.");
    }

    @Test
    void getRequestsByUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> requestService.getRequestsByUser(100L));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void getAllRequests() {
        UserDto createdUser = userService.createUser(userDto);
        requestService.createRequest(createdUser.getId(), requestDto);
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Test2");
        newUserDto.setEmail("newtest@test.ru");
        userDto.setEmail("newtest@test.ru");
        UserDto otherUserDto = userService.createUser(newUserDto);
        List<ItemRequestDto> requests = requestService.getAllRequests(otherUserDto.getId(), 0, 10);
        assertEquals(1, requests.size(), "Размер списка не совпадает.");
        assertEquals("test", requests.get(0).getDescription());
    }

    @Test
    void getAllRequestsUserNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        requestService.createRequest(createdUser.getId(), requestDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> requestService.getAllRequests(100L, 0, 10));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void getRequestByIdUserNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        ItemRequestDto createdRequest = requestService.createRequest(createdUser.getId(), requestDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> requestService.getRequestById(100L, createdRequest.getId()));
        assertEquals("Пользователь с id=100 не существует.", ex.getMessage());
    }

    @Test
    void getRequestByIdNotExist() {
        UserDto createdUser = userService.createUser(userDto);
        ItemRequestDto createdRequest = requestService.createRequest(createdUser.getId(), requestDto);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class,
                () -> requestService.getRequestById(createdUser.getId(), 100L));
        assertEquals("Запрос с id=100 не существует.", ex.getMessage());
    }
}
