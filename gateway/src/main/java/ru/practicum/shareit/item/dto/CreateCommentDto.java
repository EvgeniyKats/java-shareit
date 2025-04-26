package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateCommentDto {
    @NotBlank
    @Size(max = 500)
    private String text;
    private final LocalDateTime createdTime = LocalDateTime.now();
}
