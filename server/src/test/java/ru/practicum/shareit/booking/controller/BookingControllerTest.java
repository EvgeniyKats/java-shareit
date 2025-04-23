package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final List<GetBookingDto> listAns = List.of(new GetBookingDto());
    private static final GetBookingDto singleAns = new GetBookingDto();
    private static final String API_PREFIX = "/bookings";

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getBookingById_Success() throws Exception {
        long userId = 1;
        long bookingId = 1;
        String path = API_PREFIX + "/" + bookingId;

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(singleAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsById_BadBookingId() throws Exception {
        long userId = 1;
        long bookingId = 0;
        String path = API_PREFIX + "/" + bookingId;

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(singleAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsById_BadUserId() throws Exception {
        long userId = 0;
        long bookingId = 1;
        String path = API_PREFIX + "/" + bookingId;

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(singleAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsForBooker() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(bookingService.getBookingsForBooker(any(), anyLong()))
                .thenReturn(listAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsForOwner() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/owner";

        when(bookingService.getBookingsForOwner(any(), anyLong()))
                .thenReturn(listAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(singleAns);

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

        when(bookingService.makeDecisionForBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(singleAns);

        mockMvc.perform(patch(path)
                        .param("approved", "true")
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }
}