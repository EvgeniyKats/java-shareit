package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

public interface UserService {
    GetUserDto getUserById(Long id);

    GetUserDto createUser(CreateUserDto createUserDto);

    GetUserDto updateUser(Long id, UpdateUserDto updateUserDto);

    void deleteUserById(Long id);
}
