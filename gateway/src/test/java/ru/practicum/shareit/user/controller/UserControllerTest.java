package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    private static final String API_PREFIX = "/users";

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserById() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/" + userId;

        when(userClient.getUserById(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get(path))
                .andExpect(status().isOk());
    }

    @Test
    void createUser() throws Exception {
        String path = API_PREFIX;

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("name");
        createUserDto.setEmail("email@example.com");

        when(userClient.createUser(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateUser() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/" + userId;

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("newName");

        when(userClient.updateUser(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserById() throws Exception {
        long userId = 1;
        String path = API_PREFIX + "/" + userId;

        mockMvc.perform(delete(path))
                .andExpect(status().isNoContent());
    }
}