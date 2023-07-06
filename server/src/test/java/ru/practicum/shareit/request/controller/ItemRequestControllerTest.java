package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.item.model.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Long USER_ID = 1L;
    private static final Long ITEM_REQUEST_ID = 2L;


    @Test
    public void testCreateItemRequest() throws Exception {
        ItemRequestDto itemRequestDto = createItemRequestDto();
        when(itemRequestService.createItemRequest(itemRequestDto, USER_ID)).thenReturn(itemRequestDto);

        MockHttpServletRequestBuilder request = post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", USER_ID)
                .content(objectMapper.writeValueAsString(itemRequestDto));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(itemRequestDto.getCreated())));
    }

    @Test
    public void testGetOwnerRequests() throws Exception {
        ItemRequestDto itemRequestDto = createItemRequestDto();
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        itemRequestDtoList.add(itemRequestDto);
        when(itemRequestService.getItemRequests(USER_ID)).thenReturn(itemRequestDtoList);

        MockHttpServletRequestBuilder request = get("/requests")
                .header("X-Sharer-User-Id", USER_ID);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.[0].created").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(itemRequestDto.getCreated())));
    }

    @Test
    public void testGetOtherRequests() throws Exception {
        ItemRequestDto itemRequestDto = createItemRequestDto();
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        itemRequestDtoList.add(itemRequestDto);
        when(itemRequestService.getItemWithPagination(USER_ID, 0, 10)).thenReturn(itemRequestDtoList);

        MockHttpServletRequestBuilder request = get("/requests/all")
                .header("X-Sharer-User-Id", USER_ID)
                .param("from", "0")
                .param("size", "10");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.[0].created").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(itemRequestDto.getCreated())));
    }

    @Test
    public void testGetRequest() throws Exception {
        ItemRequestDto itemRequestDto = createItemRequestDto();
        when(itemRequestService.getItemRequest(ITEM_REQUEST_ID, USER_ID)).thenReturn(itemRequestDto);

        MockHttpServletRequestBuilder request = get("/requests/" + ITEM_REQUEST_ID)
                .header("X-Sharer-User-Id", USER_ID);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(itemRequestDto.getCreated())));
    }

    private ItemRequestDto createItemRequestDto() {
        ItemDtoForRequest itemDtoForRequest = new ItemDtoForRequest();
        itemDtoForRequest.setId(1L);
        itemDtoForRequest.setName("item name");
        itemDtoForRequest.setDescription("item description");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(ITEM_REQUEST_ID);
        itemRequestDto.setDescription("item request description");
        itemRequestDto.setCreated(LocalDateTime.of(2021, 6, 17, 12, 0));
        List<ItemDtoForRequest> itemDtoForRequestList = new ArrayList<>();
        itemDtoForRequestList.add(itemDtoForRequest);
        itemRequestDto.setItems(itemDtoForRequestList);
        return itemRequestDto;
    }
}