package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CreateItemRequestDto {
    @NotBlank
    @Size(max = 300)
    private String description;
    @JsonIgnore
    private final LocalDateTime createdTime = LocalDateTime.now();
}
