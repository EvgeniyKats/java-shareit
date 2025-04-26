package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.HttpHeaderNames.HEADER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final String API_PREFIX = "/items";

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private CreateItemDto createItemDto;
    private UpdateItemDto updateItemDto;
    private CreateCommentDto createCommentDto;

    @BeforeEach
    void beforeEach() {
        createCommentDto = new CreateCommentDto();
        createCommentDto.setText("text");

        createItemDto = new CreateItemDto();
        createItemDto.setName("name");
        createItemDto.setDescription("description");
        createItemDto.setAvailable(true);

        updateItemDto = new UpdateItemDto();
        updateItemDto.setName("newName");
    }

    @Test
    void getItemById() throws Exception {
        long itemId = 1;
        long userId = 1;
        String path = API_PREFIX + "/" + itemId;

        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByUserId() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(itemClient.getItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void searchByText() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/search";

        when(itemClient.getItemsByText(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path)
                        .param("text", "text")
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void createItem() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(itemClient.createItem(any(), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(post(path)
                        .content(objectMapper.writeValueAsString(createItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isCreated());
    }

    @Test
    void commentItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        String path = API_PREFIX + "/" + itemId + "/comment";

        when(itemClient.commentItem(any(), anyLong(), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(post(path)
                        .content(objectMapper.writeValueAsString(createCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isCreated());
    }

    @Test
    void updateItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        String path = API_PREFIX + "/" + itemId;

        when(itemClient.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(path)
                        .content(objectMapper.writeValueAsString(updateItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        String path = API_PREFIX + "/" + itemId;

        mockMvc.perform(delete(path)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isNoContent());
    }
}