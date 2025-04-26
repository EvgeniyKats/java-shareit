package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.HttpHeaderNames.HEADER_USER_ID;
import static ru.practicum.shareit.PageConfig.DEFAULT_SIZE;

@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
@RestController
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public GetBookingDto getBookingById(@Min(1) @PathVariable Long bookingId,
                                        @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен GET /bookings/{}, userId = {}", bookingId, userId);
        GetBookingDto ans = bookingService.getBookingById(bookingId, userId);
        log.info("Бронирование с id = {}, успешно получено", bookingId);
        return ans;
    }

    @GetMapping
    public List<GetBookingDto> getBookingsForBooker(@RequestParam(defaultValue = "ALL") StateParam state,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = DEFAULT_SIZE) Integer size,
                                                    @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен GET /bookings, state = {}, userId = {}, from = {}, size = {}", state, userId, from, size);
        List<GetBookingDto> ans = bookingService.getBookingsForBooker(state, userId, from, size);
        log.info("Найдено {} бронирований для пользователя с id = {}", ans.size(), userId);
        return ans;
    }

    @GetMapping("/owner")
    public List<GetBookingDto> getBookingsForOwner(@RequestParam(defaultValue = "ALL") StateParam state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = DEFAULT_SIZE) Integer size,
                                                   @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен GET /bookings/owner, state = {}, userId = {}", state, userId);
        List<GetBookingDto> ans = bookingService.getBookingsForOwner(state, userId, from, size);
        log.info("Найдено {} бронирований для владельца с id = {}", ans.size(), userId);
        return ans;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GetBookingDto createBooking(@Valid @RequestBody CreateBookingDto createBookingDto,
                                       @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен POST /bookings userId = {}", userId);
        GetBookingDto ans = bookingService.createBooking(createBookingDto, userId);
        log.info("Бронирование было успешно создано пользователем {}", userId);
        return ans;
    }

    @PatchMapping("/{bookingId}")
    public GetBookingDto makeDecisionForBooking(@Min(1) @PathVariable Long bookingId,
                                                @RequestParam Boolean approved,
                                                @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен PATH /bookings/{} approved = {}, userId = {}", bookingId, approved, userId);
        GetBookingDto ans = bookingService.makeDecisionForBooking(bookingId, approved, userId);
        log.info("Решение по бронированию id = {}, изменено на {}", ans.getId(), ans.getStatus());
        return ans;
    }

    public enum StateParam {
        ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED
    }
}
