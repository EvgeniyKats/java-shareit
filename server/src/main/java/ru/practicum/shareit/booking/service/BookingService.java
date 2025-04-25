package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

import static ru.practicum.shareit.booking.controller.BookingController.StateParam;

import java.util.List;

public interface BookingService {
    GetBookingDto getBookingById(Long bookingId, Long userId);

    List<GetBookingDto> getBookingsForBooker(StateParam state, Long userId, Integer from, Integer size);

    List<GetBookingDto> getBookingsForOwner(StateParam state, Long userId, Integer from, Integer size);

    GetBookingDto createBooking(CreateBookingDto createBookingDto, Long userId);

    GetBookingDto makeDecisionForBooking(Long bookingId, Boolean approved, Long userId);
}
