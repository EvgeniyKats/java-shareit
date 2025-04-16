package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

public interface BookingStartEnd {
    Long getId();

    LocalDateTime getStartBookingTime();

    LocalDateTime getEndBookingTime();
}
