package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.controller.ItemController;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String API_PREFIX = "/bookings";

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getBookingById_Success() throws Exception {
        long userId = 1;
        long bookingId = 1;
        String path = API_PREFIX + "/" + bookingId;

        when(bookingClient.getBookings(anyInt(), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsById_BadBookingId() throws Exception {
        long userId = 1;
        long bookingId = 0;
        String path = API_PREFIX + "/" + bookingId;

        when(bookingClient.getBookings(anyInt(), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsById_BadUserId() throws Exception {
        long userId = 0;
        long bookingId = 1;
        String path = API_PREFIX + "/" + bookingId;

        when(bookingClient.getBookings(anyInt(), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsForBooker() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(bookingClient.getBookings(anyLong(), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsForOwner() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/owner";

        when(bookingClient.getBookings(anyLong(), any(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setStartBookingTime(LocalDateTime.now().plusHours(1));
        createBookingDto.setEndBookingTime(LocalDateTime.now().plusDays(1));
        createBookingDto.setItemId(1L);

        mockMvc.perform(post(path)
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isCreated());
    }

    @Test
    void makeDecisionForBooking() throws Exception {
        long bookingId = 1;
        long userId = 1;
        String path = API_PREFIX + "/" + bookingId;

        when(bookingClient.makeDecisionForBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(path)
                        .param("approved", "true")
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }
}