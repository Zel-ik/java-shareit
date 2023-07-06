package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoFrom;
import ru.practicum.shareit.booking.model.dto.BookingDtoTo;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.model.dto.UserDtoForBooking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    public void testCreateBooking() throws Exception {
        BookingDtoFrom bookingDtoFrom = new BookingDtoFrom(null,
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusMinutes(11),
                null,
                new UserDtoForBooking(123L).getId(),
                new ItemDtoForBooking(456L, "Item name").getId());
        long userId = 123;
        BookingDtoTo expectedBooking = new BookingDtoTo(1L,
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusMinutes(11),
                BookingStatus.WAITING,
                new UserDtoForBooking(123L),
                new ItemDtoForBooking(456L, "Item name"));

        given(bookingService.createBooking(bookingDtoFrom, userId)).willReturn(expectedBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookingDtoFrom)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedBooking.getId()))
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBooking.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBooking.getEnd())))
                .andExpect(jsonPath("$.status").value(expectedBooking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(expectedBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(expectedBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(expectedBooking.getItem().getName()));

        verify(bookingService, times(1)).createBooking(bookingDtoFrom, userId);
    }

    @Test
    public void testMakeApprovedBooking() throws Exception {
        long userId = 123;
        long bookingId = 456;
        boolean approved = true;
        BookingDtoTo expectedBooking = new BookingDtoTo(1L,
                LocalDateTime.of(2023, 6, 18, 10, 0),
                LocalDateTime.of(2023, 6, 19, 12, 0),
                BookingStatus.APPROVED,
                new UserDtoForBooking(123L),
                new ItemDtoForBooking(456L, "Item name"));

        given(bookingService.makeApprovedBooking(userId, bookingId, approved)).willReturn(expectedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedBooking.getId()))
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBooking.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBooking.getEnd())))
                .andExpect(jsonPath("$.status").value(expectedBooking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(expectedBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(expectedBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(expectedBooking.getItem().getName()));

        verify(bookingService, times(1)).makeApprovedBooking(userId, bookingId, approved);
    }

    @Test
    public void testGetBooking() throws Exception {
        long userId = 123;
        long bookingId = 456;
        BookingDtoTo expectedBooking = new BookingDtoTo(1L,
                LocalDateTime.of(2023, 6, 18, 10, 0),
                LocalDateTime.of(2023, 6, 19, 12, 0),
                BookingStatus.APPROVED,
                new UserDtoForBooking(123L),
                new ItemDtoForBooking(456L, "Item name"));

        given(bookingService.getBooking(userId, bookingId)).willReturn(expectedBooking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedBooking.getId()))
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBooking.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBooking.getEnd())))
                .andExpect(jsonPath("$.status").value(expectedBooking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(expectedBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(expectedBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(expectedBooking.getItem().getName()));

        verify(bookingService, times(1)).getBooking(userId, bookingId);
    }

    @Test
    public void testGetUserBookings() throws Exception {
        long userId = 123;
        String state = "ALL";
        int from = 0;
        int size = 10;
        List<BookingDtoTo> expectedBookings = Arrays.asList(
                new BookingDtoTo(1L,
                        LocalDateTime.of(2023, 6, 18, 10, 0),
                        LocalDateTime.of(2023, 6, 19, 12, 0),
                        BookingStatus.APPROVED,
                        new UserDtoForBooking(123L),
                        new ItemDtoForBooking(456L, "Item 1 name")),
                new BookingDtoTo(2L,
                        LocalDateTime.of(2023, 6, 22, 14, 0),
                        LocalDateTime.of(2023, 6, 23, 16, 0),
                        BookingStatus.APPROVED,
                        new UserDtoForBooking(456L),
                        new ItemDtoForBooking(789L, "Item 2 name")),
                new BookingDtoTo(3L,
                        LocalDateTime.of(2023, 6, 25, 9, 0),
                        LocalDateTime.of(2023, 6, 26, 11, 0),
                        BookingStatus.REJECTED,
                        new UserDtoForBooking(789L),
                        new ItemDtoForBooking(123L, "Item 3 name"))
        );

        given(bookingService.getUserBookings(userId, state, from, size)).willReturn(expectedBookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedBookings.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(0).getStart())))
                .andExpect(jsonPath("$[0].end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(0).getEnd())))
                .andExpect(jsonPath("$[0].status").value(expectedBookings.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].booker.id").value(expectedBookings.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(expectedBookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(expectedBookings.get(0).getItem().getName()))
                .andExpect(jsonPath("$[1].id").value(expectedBookings.get(1).getId()))
                .andExpect(jsonPath("$[1].start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(1).getStart())))
                .andExpect(jsonPath("$[1].end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(1).getEnd())))
                .andExpect(jsonPath("$[1].status").value(expectedBookings.get(1).getStatus().toString()))
                .andExpect(jsonPath("$[1].booker.id").value(expectedBookings.get(1).getBooker().getId()))
                .andExpect(jsonPath("$[1].item.id").value(expectedBookings.get(1).getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(expectedBookings.get(1).getItem().getName()))
                .andExpect(jsonPath("$[2].id").value(expectedBookings.get(2).getId()))
                .andExpect(jsonPath("$[2].start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(2).getStart())))
                .andExpect(jsonPath("$[2].end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(2).getEnd())))
                .andExpect(jsonPath("$[2].status").value(expectedBookings.get(2).getStatus().toString()))
                .andExpect(jsonPath("$[2].booker.id").value(expectedBookings.get(2).getBooker().getId()))
                .andExpect(jsonPath("$[2].item.id").value(expectedBookings.get(2).getItem().getId()))
                .andExpect(jsonPath("$[2].item.name").value(expectedBookings.get(2).getItem().getName()));

        verify(bookingService, times(1)).getUserBookings(userId, state, from, size);
    }

    @Test
    public void testGetOwnerBookings() throws Exception {
        long userId = 123;
        String state = "ALL";
        int from = 0;
        int size = 10;
        List<BookingDtoTo> expectedBookings = Arrays.asList(
                new BookingDtoTo(1L,
                        LocalDateTime.of(2023, 6, 18, 10, 0),
                        LocalDateTime.of(2023, 6, 19, 12, 0),
                        BookingStatus.APPROVED,
                        new UserDtoForBooking(123L),
                        new ItemDtoForBooking(456L, "Item 1 name")),
                new BookingDtoTo(2L,
                        LocalDateTime.of(2023, 6, 22, 14, 0),
                        LocalDateTime.of(2023, 6, 23, 16, 0),
                        BookingStatus.APPROVED,
                        new UserDtoForBooking(456L),
                        new ItemDtoForBooking(789L, "Item 2 name")),
                new BookingDtoTo(3L,
                        LocalDateTime.of(2023, 6, 25, 9, 0),
                        LocalDateTime.of(2023, 6, 26, 11, 0),
                        BookingStatus.REJECTED,
                        new UserDtoForBooking(789L),
                        new ItemDtoForBooking(123L, "Item 3 name"))
        );

        given(bookingService.getOwnerBookings(userId, state, from, size)).willReturn(expectedBookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(jsonPath("$[0].id").value(expectedBookings.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(0).getStart())))
                .andExpect(jsonPath("$[0].end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(0).getEnd())))
                .andExpect(jsonPath("$[0].status").value(expectedBookings.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].booker.id").value(expectedBookings.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(expectedBookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(expectedBookings.get(0).getItem().getName()))
                .andExpect(jsonPath("$[1].id").value(expectedBookings.get(1).getId()))
                .andExpect(jsonPath("$[1].start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(1).getStart())))
                .andExpect(jsonPath("$[1].end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(1).getEnd())))
                .andExpect(jsonPath("$[1].status").value(expectedBookings.get(1).getStatus().toString()))
                .andExpect(jsonPath("$[1].booker.id").value(expectedBookings.get(1).getBooker().getId()))
                .andExpect(jsonPath("$[1].item.id").value(expectedBookings.get(1).getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(expectedBookings.get(1).getItem().getName()))
                .andExpect(jsonPath("$[2].id").value(expectedBookings.get(2).getId()))
                .andExpect(jsonPath("$[2].start").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(2).getStart())))
                .andExpect(jsonPath("$[2].end").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(expectedBookings.get(2).getEnd())))
                .andExpect(jsonPath("$[2].status").value(expectedBookings.get(2).getStatus().toString()))
                .andExpect(jsonPath("$[2].booker.id").value(expectedBookings.get(2).getBooker().getId()))
                .andExpect(jsonPath("$[2].item.id").value(expectedBookings.get(2).getItem().getId()))
                .andExpect(jsonPath("$[2].item.name").value(expectedBookings.get(2).getItem().getName()));

        verify(bookingService, times(1)).getOwnerBookings(userId, state, from, size);
    }
}