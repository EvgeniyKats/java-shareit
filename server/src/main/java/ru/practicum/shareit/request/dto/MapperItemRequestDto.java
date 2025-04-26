package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperItemRequestDto {
    ItemRequest mapCreateToModel(CreateItemRequestDto createItemRequestDto);

    GetItemRequestDto mapModelToGetDto(ItemRequest itemRequest);
}
