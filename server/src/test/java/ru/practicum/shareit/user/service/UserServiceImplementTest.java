package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.custom.DuplicateException;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.UtilTest.createUserDtos;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplementTest {
    private final UserService userService;

    @Test
    void getUserById() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        GetUserDto getUserDto = userService.getUserById(userId);
        assertEquals(userId, getUserDto.getId());
        assertEquals(userCreate.getName(), getUserDto.getName());
        assertEquals(userCreate.getEmail(), getUserDto.getEmail());
    }

    @Test
    void createUser() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        GetUserDto getUserDto = userService.createUser(userCreate);

        assertNotNull(getUserDto.getId());
        assertEquals(userCreate.getName(), getUserDto.getName());
        assertEquals(userCreate.getEmail(), getUserDto.getEmail());
    }

    @Test
    void createUserThrowsDuplicate() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        userService.createUser(userCreate);
        assertThrows(DuplicateException.class, () -> userService.createUser(userCreate));
    }

    @Test
    void updateUser() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("newName");
        updateUserDto.setEmail("newEmail@example.com");
        userService.updateUser(userId, updateUserDto);

        GetUserDto getUserDto = userService.getUserById(userId);
        assertEquals(userId, getUserDto.getId());
        assertEquals(updateUserDto.getName(), getUserDto.getName());
        assertEquals(updateUserDto.getEmail(), getUserDto.getEmail());
    }

    @Test
    void deleteUserById() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        userService.deleteUserById(userId);

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }
}