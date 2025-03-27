package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetItemDto {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    @JsonProperty(value = "available")
    private Boolean isAvailable;
}
