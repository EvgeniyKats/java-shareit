package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetBookingDto {
    private Long id;
    private Item item;
    private User booker;
    private StatusBookingDto status;
    @JsonProperty(value = "start")
    private LocalDateTime startBookingTime;
    @JsonProperty(value = "end")
    private LocalDateTime endBookingTime;
}
