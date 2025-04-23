package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateBookingDto {
    @NotNull
    @Min(1)
    private Long itemId;

    @NotNull
    @FutureOrPresent
    @JsonProperty(value = "start")
    private LocalDateTime startBookingTime;

    @NotNull
    @Future
    @JsonProperty(value = "end")
    private LocalDateTime endBookingTime;

    private StatusBookingDto status = StatusBookingDto.WAITING;
}
