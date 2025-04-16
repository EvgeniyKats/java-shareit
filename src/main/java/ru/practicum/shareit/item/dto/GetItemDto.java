package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStartEnd;

import java.util.List;

@Getter
@Setter
public class GetItemDto {
    private Long id;
    @JsonProperty(value = "owner")
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    @JsonProperty(value = "lastBooking")
    private BookingStartEnd lastBookingTime;
    @JsonProperty(value = "nextBooking")
    private BookingStartEnd nextBookingTime;
    private List<GetCommentDto> comments;
}
