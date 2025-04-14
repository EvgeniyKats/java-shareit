package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetItemDto {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Boolean available;
}
