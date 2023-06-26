package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentOutDto;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @InjectMocks
    private ItemController controller;
    @Mock
    private ItemService itemService;
    private MockMvc mvc;
    private ItemDto itemDto;
    private User user;
    private Comment comment;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        user = User.builder()
                .id(1L)
                .email("artem@yandex.com")
                .name("Artem")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description item")
                .available(true)
                .requestId(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .item(ItemMapper.toItem(itemDto, user))
                .author(user)
                .text("Cool!")
                .build();
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemUpdate = ItemDto.builder()
                .id(1L)
                .name("ItemUp")
                .description("Description itemUp")
                .available(false)
                .build();
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemUpdate);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemUpdate.getId()))
                .andExpect(jsonPath("$.name").value(itemUpdate.getName()))
                .andExpect(jsonPath("$.description").value(itemUpdate.getDescription()));
    }

    @Test
    void getItemById() throws Exception {
        ItemBookingDto itemBookingDto = ItemBookingDto.builder()
                .id(2L)
                .name("itemBookingDto")
                .description("Description item")
                .available(false)
                .build();
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemBookingDto);
        mvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemBookingDto.getId()))
                .andExpect(jsonPath("$.name").value(itemBookingDto.getName()))
                .andExpect(jsonPath("$.description").value(itemBookingDto.getDescription()));
    }

    @Test
    void getAllItem() throws Exception {
        List<ItemBookingDto> allItem = List.of(
                ItemBookingDto.builder()
                        .id(2L)
                        .name("itemBookingDto")
                        .description("Description item")
                        .available(false)
                        .build(),
                ItemBookingDto.builder()
                        .id(3L)
                        .name("itemBookingDto2")
                        .description("Description item2")
                        .available(true)
                        .build()
        );
        when(itemService.getAllItem(anyLong()))
                .thenReturn(allItem);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void searchItem() throws Exception {
        List<ItemDto> allItem = List.of(
                ItemDto.builder()
                        .id(2L)
                        .name("Ручка")
                        .description("Описание")
                        .available(true)
                        .build(),
                ItemDto.builder()
                        .id(3L)
                        .name("Отвертка крестовая")
                        .description("Description item2 ручка")
                        .available(true)
                        .build()
        );
        when(itemService.searchItem(any(String.class), anyLong()))
                .thenReturn(allItem);
        mvc.perform(get("/items/search?text=ручка")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void addComment() throws Exception {
        CommentOutDto commentOutDto = new CommentOutDto(comment.getText());
        when(itemService.addComment(anyLong(), anyLong(), any(CommentOutDto.class)))
                .thenReturn(CommentMapper.toDto(comment));
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentOutDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentOutDto.getText()));
    }
}