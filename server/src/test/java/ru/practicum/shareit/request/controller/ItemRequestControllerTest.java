package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static List<GetItemRequestDto> listAns = List.of(new GetItemRequestDto());
    private static GetItemRequestDto singleAns = new GetItemRequestDto();
    private static final String API_PREFIX = "/requests";

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserItemRequests() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(itemRequestService.getUserItemRequests(anyLong()))
                .thenReturn(listAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                        .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequests() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/all";

        when(itemRequestService.getAllItemRequests(anyLong()))
                .thenReturn(listAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestById() throws Exception {
        long userId = 1;
        long requestId = 1;
        String path = API_PREFIX + "/" + requestId;

        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(singleAns);

        mockMvc.perform(get(path)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void createItemRequest() throws Exception {
        long userId = 1;
        String path = API_PREFIX;

        when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenReturn(singleAns);

        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();
        createItemRequestDto.setDescription("description");

        mockMvc.perform(post(path)
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(ItemController.HEADER_USER_ID, userId))
                .andExpect(status().isCreated());
    }
}