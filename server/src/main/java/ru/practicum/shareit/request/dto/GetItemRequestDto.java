package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetItemRequestDto {
    private Long id;

    private String description;

    private Long ownerId;

    @JsonProperty(value = "created")
    private LocalDateTime createdTime;

    private List<Item> items = new ArrayList<>();
}
