package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapperUserDto {
    public static User createDtoToUser(CreateUserDto createUserDto) {
        return User.builder()
                .email(createUserDto.getEmail())
                .name(createUserDto.getName())
                .build();
    }

    public static User updateDtoToUser(UpdateUserDto updateUserDto) {
        return User.builder()
                .email(updateUserDto.getEmail())
                .name(updateUserDto.getName())
                .build();
    }

    public static GetUserDto userToGetDto(User user) {
        return GetUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
