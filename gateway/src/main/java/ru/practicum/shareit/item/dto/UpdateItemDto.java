package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.exception.custom.IncorrectParameterException;

@Getter
@Setter
public class UpdateItemDto {
    @Size(max = 100)
    private String name;
    @Size(max = 300)
    private String description;
    @JsonProperty(value = "available")
    private Boolean available;
    private Long requestId;

    public void setName(String name) {
        if (name != null) {
            this.name = name.trim();
            if (name.isBlank()) throw new IncorrectParameterException("Имя не может быть пустым");
        }
    }

    public void setDescription(String description) {
        if (description != null) this.description = description.trim();
    }
}
