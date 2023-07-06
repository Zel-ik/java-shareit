package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constans.State;
import ru.practicum.shareit.constans.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @InjectMocks
    private BookingController controller;
    @Mock
    private BookingService bookingService;
    private MockMvc mvc;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingDtoIn bookingDtoIn;
    private BookingDto bookingDtoStat;
    private User user;
    private User otherUser;
    private Item item;
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
                .email("artem@yandex.com")
                .name("Artem")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description item")
                .available(true)
                .owner(otherUser)
                .requestId(1L)
                .build();

        bookingDtoIn = BookingDtoIn.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .bookerId(user.getId())
                .status("WAITING")
                .build();
        bookingDtoStat = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(Status.WAITING)
                .build();
        booking = BookingMapper.toBooking(bookingDtoIn, user, item);
        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void create() throws Exception {
        when(bookingService.saveBooking(any(BookingDtoIn.class), anyLong())).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void confirmBooking() throws Exception {
        when(bookingService.confirmBooking(true, bookingDtoStat.getId(), otherUser.getId())).thenReturn(bookingDtoStat);
        mvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", otherUser.getId())
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoStat)));
    }

    @Test
    void confirmBookingFalse() throws Exception {
        when(bookingService.confirmBooking(anyBoolean(), anyLong(), anyLong())).thenReturn(bookingDtoStat);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", otherUser.getId())
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoStat)));
    }

    @Test
    void getAllBookingWithStateAll() throws Exception {
        when(bookingService.getAllBooking(anyLong(), any(State.class), any(), any()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllBookingForItemsAll() throws Exception {
        when(bookingService.getAllBookingForItems(anyLong(), any(State.class), any(), any()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }
}