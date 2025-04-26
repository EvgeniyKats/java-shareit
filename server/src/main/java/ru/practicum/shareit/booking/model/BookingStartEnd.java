package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

public interface BookingStartEnd {
    Long getItemId();

    LocalDateTime getStartBookingTime();

    LocalDateTime getEndBookingTime();
}
