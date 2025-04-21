package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperUserDto {

    User createDtoToUser(CreateUserDto createUserDto);

    User updateDtoToUser(UpdateUserDto updateUserDto);

    GetUserDto userToGetDto(User user);
}
