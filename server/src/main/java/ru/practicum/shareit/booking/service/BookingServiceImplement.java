package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.dto.MapperBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.exception.custom.BadRequestException;
import ru.practicum.shareit.exception.custom.ForbiddenException;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImplement implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MapperBookingDto mapperBookingDto;

    @Override
    public GetBookingDto getBookingById(Long bookingId, Long userId) {
        log.info("Получение booking {} от пользователя {}", bookingId, userId);
        Optional<Booking> booking = bookingRepository.findByIdWithUserAccess(bookingId, userId);
        if (booking.isEmpty()) throwNotFoundForBookingId(bookingId);
        log.info("Букинг с id = {} найден", bookingId);
        return mapperBookingDto.bookingToGetDto(booking.get());
    }

    @Override
    public List<GetBookingDto> getBookingsForBooker(final BookingController.StateParam state, Long bookerId) {
        log.info("Получение bookings state = {}, bookerId = {}", state, bookerId);
        getUserByIdOrThrowNotFound(bookerId);

        List<Booking> bookings;

        Sort sortDescByStartBookingTime = Sort.by(Sort.Direction.DESC, "startBookingTime");

        switch (state) {
            case ALL -> bookings = bookingRepository.findByBookerId(bookerId, sortDescByStartBookingTime);
            case PAST -> bookings = bookingRepository
                    .findByBookerIdAndStartBookingTimeBefore(bookerId, LocalDateTime.now(), sortDescByStartBookingTime);
            case FUTURE -> bookings = bookingRepository
                    .findByBookerIdAndEndBookingTimeAfter(bookerId, LocalDateTime.now(), sortDescByStartBookingTime);
            case CURRENT -> bookings = bookingRepository
                    .findCurrentBookingsForBooker(bookerId, sortDescByStartBookingTime);
            case WAITING -> bookings = bookingRepository
                    .findByBookerIdAndStatus(bookerId, StatusBooking.WAITING, sortDescByStartBookingTime);
            case REJECTED -> bookings = bookingRepository
                    .findByBookerIdAndStatus(bookerId, StatusBooking.REJECTED, sortDescByStartBookingTime);
            default -> {
                log.warn("Неизвестный параметр state = {}, booker", state);
                bookings = new ArrayList<>();
            }
        }

        log.info("Количество букингов = {}, state = {}, bookerId = {}", bookings.size(), state, bookerId);
        return bookings.stream()
                .map(mapperBookingDto::bookingToGetDto)
                .toList();
    }

    @Override
    public List<GetBookingDto> getBookingsForOwner(BookingController.StateParam state, Long ownerId) {
        log.info("Получение bookings state = {}, ownerId = {}", state, ownerId);
        getUserByIdOrThrowNotFound(ownerId);

        List<Booking> bookings;

        Sort sortDescByStartBookingTime = Sort.by(Sort.Direction.DESC, "startBookingTime");

        switch (state) {
            case ALL -> bookings = bookingRepository.findAllBookingsForOwner(ownerId, sortDescByStartBookingTime);
            case PAST -> bookings = bookingRepository
                    .findPastBookingsForOwner(ownerId, LocalDateTime.now(), sortDescByStartBookingTime);
            case FUTURE -> bookings = bookingRepository
                    .findFutureBookingsForOwner(ownerId, LocalDateTime.now(), sortDescByStartBookingTime);
            case CURRENT -> bookings = bookingRepository
                    .findCurrentBookingsForOwner(ownerId, sortDescByStartBookingTime);
            case WAITING -> bookings = bookingRepository
                    .findByStatusForOwner(ownerId, StatusBooking.WAITING, sortDescByStartBookingTime);
            case REJECTED -> bookings = bookingRepository
                    .findByStatusForOwner(ownerId, StatusBooking.REJECTED, sortDescByStartBookingTime);
            default -> {
                log.warn("Неизвестный параметр state = {}, owner", state);
                bookings = new ArrayList<>();
            }
        }

        log.info("Количество букингов = {}, state = {}, ownerId = {}", bookings.size(), state, ownerId);
        return bookings.stream()
                .map(mapperBookingDto::bookingToGetDto)
                .toList();
    }

    @Transactional
    @Override
    public GetBookingDto createBooking(CreateBookingDto createBookingDto, Long userId) {
        log.info("Создание букинга для пользователя {}", userId);
        if (createBookingDto.getStartBookingTime().isAfter(createBookingDto.getEndBookingTime())) {
            throw new BadRequestException("Время начала аренды не может быть раньше её окончания.");
        }

        User booker = getUserByIdOrThrowNotFound(userId);
        Item item = getItemByIdOrThrowNotFound(createBookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new BadRequestException("Предмет недоступен для аренды id = " + item.getId());
        }

        log.trace("Проверка, пытается ли владелец арендовать свою же вещь");
        if (item.getOwnerId().equals(userId)) {
            throw new BadRequestException("Владелец не может арендатором");
        }
        log.trace("Проверка попытку арендовать свою вещь пройдена");

        Booking booking = mapperBookingDto.createDtoToBooking(createBookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        return mapperBookingDto.bookingToGetDto(booking);
    }

    @Transactional
    @Override
    public GetBookingDto makeDecisionForBooking(Long bookingId, Boolean approved, Long userId) {
        log.info("Попытка изменить статус букинга bookingId={}, approved={}, userId={}", bookingId, approved, userId);
        Optional<Booking> bookingOptional = bookingRepository
                .findByIdAndStatus(bookingId, StatusBooking.WAITING);

        if (bookingOptional.isEmpty()) throwNotFoundForBookingId(bookingId);

        Booking booking = bookingOptional.get();

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new ForbiddenException("Принять предложение об аренде может только владелец предмета.");
        }

        if (approved) {
            booking.setStatus(StatusBooking.APPROVED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }

        log.info("Текущий статус букинга bookingId={}, status={}", booking.getId(), booking.getStatus());
        return mapperBookingDto.bookingToGetDto(booking);
    }

    private void throwNotFoundForBookingId(Long id) {
        throw NotFoundException.builder()
                .setNameObject("Букинг")
                .setNameParameter("id")
                .setValueParameter(id)
                .build();
    }

    private User getUserByIdOrThrowNotFound(Long userId) {
        log.trace("Проверка на существования пользователя с userId = {}", userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw NotFoundException.builder()
                    .setNameObject("Пользователь")
                    .setNameParameter("userId")
                    .setValueParameter(userId)
                    .build();
        }
        log.trace("Пользователь с userId = {}, найден", userId);
        return user.get();
    }

    private Item getItemByIdOrThrowNotFound(Long itemId) {
        log.trace("Проверка предмета с itemId = {} в хранилище", itemId);
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw NotFoundException.builder()
                    .setNameObject("Предмет")
                    .setNameParameter("itemId")
                    .setValueParameter(itemId)
                    .build();
        }
        log.trace("Предмет с itemId = {}, найден", itemId);
        return optionalItem.get();
    }
}
