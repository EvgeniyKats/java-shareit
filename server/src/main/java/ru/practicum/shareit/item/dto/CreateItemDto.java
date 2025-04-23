package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateItemDto {
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    @Size(max = 300)
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;

    public void setName(String name) {
        if (name != null) this.name = name.trim();
    }

    public void setDescription(String description) {
        if (description != null) this.description = description.trim();
    }
}
