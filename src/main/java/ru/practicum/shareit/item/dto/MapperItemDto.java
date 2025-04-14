package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperItemDto {
    Item createDtoToItem(CreateItemDto createItemDto);

    Item updateDtoToItem(UpdateItemDto updateItemDto);

    GetItemDto itemToGetDto(Item item);
}
