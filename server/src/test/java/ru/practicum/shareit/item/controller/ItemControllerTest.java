package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final List<GetItemDto> listAns = List.of(new GetItemDto());
    private static final GetItemDto singleAns = new GetItemDto();
    private static final String API_PREFIX = "/items";

    @MockBean
    private ItemService itemService;

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

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(singleAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByUserId() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(itemService.getItemsByUserId(anyLong()))
                .thenReturn(listAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void searchByText() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/search";

        when(itemService.getItemsByText(anyString(), anyLong()))
                .thenReturn(listAns);

        mockMvc.perform(get(path)
                        .param("text", "text")
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void createItem() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(itemService.createItem(any(), anyLong()))
                .thenReturn(singleAns);

        mockMvc.perform(post(path)
                        .content(objectMapper.writeValueAsString(createItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isCreated());
    }

    @Test
    void commentItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        String path = API_PREFIX + "/" + itemId + "/comment";

        when(itemService.commentItem(any(), anyLong(), anyLong()))
                .thenReturn(new GetCommentDto());

        mockMvc.perform(post(path)
                        .content(objectMapper.writeValueAsString(createCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isCreated());
    }

    @Test
    void updateItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        String path = API_PREFIX + "/" + itemId;

        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(singleAns);

        mockMvc.perform(patch(path)
                        .content(objectMapper.writeValueAsString(updateItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        String path = API_PREFIX + "/" + itemId;

        mockMvc.perform(delete(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isNoContent());
    }
}