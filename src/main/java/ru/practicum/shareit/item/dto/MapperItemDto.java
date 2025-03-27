package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapperItemDto {
    public static Item createDtoToItem(CreateItemDto createItemDto) {
        return Item.builder()
                .name(createItemDto.getName())
                .description(createItemDto.getDescription())
                .isAvailable(createItemDto.getIsAvailable())
                .build();
    }

    public static Item updateDtoToItem(UpdateItemDto updateItemDto) {
        return Item.builder()
                .name(updateItemDto.getName())
                .description(updateItemDto.getDescription())
                .isAvailable(updateItemDto.getIsAvailable())
                .build();
    }

    public static GetItemDto itemToGetDto(Item item) {
        return GetItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .build();
    }
}
