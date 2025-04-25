package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import static ru.practicum.shareit.HttpHeaderNames.HEADER_USER_ID;

@Slf4j
@RequiredArgsConstructor
@Validated
@Controller
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@Min(1) @PathVariable Long bookingId,
                                                 @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForBooker(@RequestParam(defaultValue = "ALL") StateParam state,
                                                       @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingClient.getBookings(userId, state, false);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(@RequestParam(defaultValue = "ALL") StateParam state,
                                                      @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingClient.getBookings(userId, state, true);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@Valid @RequestBody CreateBookingDto createBookingDto,
                                       @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingClient.createBooking(userId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> makeDecisionForBooking(@Min(1) @PathVariable Long bookingId,
                                                         @Min(1) @RequestHeader(HEADER_USER_ID) Long userId,
                                                         @RequestParam Boolean approved) {
        return bookingClient.makeDecisionForBooking(bookingId, userId, approved);
    }

    public enum StateParam {
        ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED
    }
}
