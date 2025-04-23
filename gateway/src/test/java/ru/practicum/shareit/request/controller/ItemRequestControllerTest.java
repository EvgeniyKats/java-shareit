package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String API_PREFIX = "/requests";

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserItemRequests() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(itemRequestClient.getUserItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                        .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequests() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/all";

        when(itemRequestClient.getAllItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestById() throws Exception {
        long userId = 1;
        long requestId = 1;
        String path = API_PREFIX + "/" + requestId;

        when(itemRequestClient.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void createItemRequest() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(itemRequestClient.createItemRequest(anyLong(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();
        createItemRequestDto.setDescription("description");

        mockMvc.perform(post(path)
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isCreated());
    }
}