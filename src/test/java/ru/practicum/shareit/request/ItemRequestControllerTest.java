package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.dto.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @InjectMocks
    private ItemRequestController controller;
    @Mock
    private ItemRequestService itemRequestService;
    private MockMvc mvc;
    private User user;
    private User otherUser;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoOut itemRequestDtoOut;
    private ItemRequestDtoIn itemRequestDtoIn;
    private LocalDateTime time = LocalDateTime.now();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        user = User.builder()
                .id(1L)
                .email("oleg@yandex.com")
                .name("oleg")
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("a@yandex.com")
                .name("artem")
                .build();

        itemRequestDtoIn = ItemRequestDtoIn.builder()
                .id(1L)
                .description("Пила")
                .userId(user.getId())
                .created(time)
                .build();

        List<ItemDto> items = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Item")
                        .description("Description item")
                        .available(true)
                        .requestId(1L)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("Item2")
                        .description("Description item2")
                        .available(true)
                        .requestId(1L)
                        .build()
        );
        itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoIn, user, time);
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(itemRequest, items);
    }

    @Test
    void addItemRequestTest() throws Exception {
        when(itemRequestService.addItemRequest(any(ItemRequestDtoIn.class), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Пила"));
    }

    @Test
    void getAllOwnRequest() throws Exception {
        when(itemRequestService.getAllOwnRequest(anyLong())).thenReturn(Collections.singletonList(itemRequestDtoOut));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Пила"));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(itemRequestDtoOut));

        mvc.perform(get("/requests/all?from=0&size=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Пила"));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn((itemRequestDtoOut));

        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Пила"));
    }
}