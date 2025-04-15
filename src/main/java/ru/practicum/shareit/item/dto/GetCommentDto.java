package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetCommentDto {
    private Long id;
    private Long authorId;
    private Long itemId;
    private String authorName;
    private String text;
    @JsonProperty(value = "created")
    private LocalDateTime createdTime;
}
