package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateComment() throws Exception {
        long userId = 1L;
        long itemId = 2L;

        CommentDto requestComment = new CommentDto(null, "test com", "test auth", LocalDateTime.now());

        CommentDto responseComment = new CommentDto(1L, "test com", "test auth", LocalDateTime.now());

        given(itemService.createComment(requestComment, userId, itemId)).willReturn(responseComment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(new ObjectMapper().writeValueAsString(requestComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("test com")))
                .andExpect(jsonPath("$.authorName", is("test auth")));
    }

    @Test
    public void createItem_ShouldReturnItemDto() throws Exception {
        long userId = 1L;

        ItemDto itemDto = new ItemDto(null, "Дрель", "Простая дрель", true, null,
                null, new ArrayList<>(), null);

        when(itemService.createItem(itemDto, userId)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(itemDto.getLastBooking()))
                .andExpect(jsonPath("$.nextBooking").value(itemDto.getNextBooking()))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));

        verify(itemService).createItem(itemDto, userId);
    }

    @Test
    public void updateItem_ShouldReturnItemDto() throws Exception {
        long itemId = 1L;
        long userId = 2L;

        ItemDto itemDto = new ItemDto(1L, "Дрель", "Простая дрель", true, null,
                null, new ArrayList<>(), null);

        when(itemService.updateItem(itemDto, itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(itemDto.getLastBooking()))
                .andExpect(jsonPath("$.nextBooking").value(itemDto.getNextBooking()))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));

        verify(itemService).updateItem(itemDto, itemId, userId);
    }

    @Test
    public void getItem_ShouldReturnItemDto() throws Exception {
        long itemId = 1L;
        long userId = 2L;

        ItemDto itemDto = new ItemDto(1L, "Дрель", "Простая дрель", true, null,
                null, new ArrayList<>(), null);

        when(itemService.getItem(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(itemDto.getLastBooking()))
                .andExpect(jsonPath("$.nextBooking").value(itemDto.getNextBooking()))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));

        verify(itemService).getItem(itemId, userId);
    }

    @Test
    public void getItems_ShouldReturnListOfItemDto() throws Exception {
        long userId = 1L;

        when(itemService.getItems(userId)).thenReturn(Collections.singletonList(new ItemDto(1L, "Дрель",
                "Простая дрель", true, null,
                null, new ArrayList<>(), null)));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].description").isString())
                .andExpect(jsonPath("$[0].available").isBoolean())
                .andExpect(jsonPath("$[0].lastBooking").doesNotExist())
                .andExpect(jsonPath("$[0].nextBooking").doesNotExist())
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].requestId").doesNotExist());

        verify(itemService).getItems(userId);
    }

    @Test
    public void getItemByText_ShouldReturnListOfItemDto() throws Exception {
        String text = "test";
        long userId = 1L;

        when(itemService.getItemsByText(text, userId)).thenReturn(Collections.singletonList(new ItemDto(1L,
                "Дрель", "Простая дрель", true, null,
                null, new ArrayList<>(), null)));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].description").isString())
                .andExpect(jsonPath("$[0].available").isBoolean())
                .andExpect(jsonPath("$[0].lastBooking").doesNotExist())
                .andExpect(jsonPath("$[0].nextBooking").doesNotExist())
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].requestId").doesNotExist());

        verify(itemService).getItemsByText(text, userId);
    }
}