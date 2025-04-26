package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.exception.custom.BadRequestException;

@Getter
@Setter
public class UpdateItemDto {
    @Size(max = 100)
    private String name;
    @Size(max = 300)
    private String description;
    @JsonProperty(value = "available")
    private Boolean available;
    @JsonProperty(defaultValue = "false")
    private Long requestId;

    public void setName(String name) {
        if (name != null) {
            if (name.isBlank()) throw new BadRequestException("Название не может быть пустым");
            this.name = name.trim();
        }
    }

    public void setDescription(String description) {
        if (description != null) this.description = description.trim();
    }
}
