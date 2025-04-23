package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetBookingDto {
    private Long id;
    private GetItemDto item;
    private GetUserDto booker;
    private StatusBookingDto status;
    @JsonProperty(value = "start")
    private LocalDateTime startBookingTime;
    @JsonProperty(value = "end")
    private LocalDateTime endBookingTime;
}
