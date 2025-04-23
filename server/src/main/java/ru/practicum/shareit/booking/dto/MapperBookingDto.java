package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperBookingDto {
    Booking createDtoToBooking(CreateBookingDto createBookingDto);

    GetBookingDto bookingToGetDto(Booking booking);
}
