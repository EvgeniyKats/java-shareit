package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static ru.practicum.shareit.UtilTest.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplementTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Test
    void getBookingById() {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();
        CreateItemDto itemCreate = createItemDtos(1).getFirst();
        long itemId = itemService.createItem(itemCreate, ownerId).getId();

        CreateUserDto bookerCreate = usersCreate.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();
        CreateBookingDto createBookingDto = createBookingDtos(1).getFirst();
        createBookingDto.setItemId(itemId);
        long bookingId = bookingService.createBooking(createBookingDto, bookerId).getId();

        GetBookingDto getBookingDto = bookingService.getBookingById(bookingId, ownerId);
        assertNotNull(getBookingDto.getId());
        assertEquals(createBookingDto.getStartBookingTime(), getBookingDto.getStartBookingTime());
        assertEquals(createBookingDto.getEndBookingTime(), getBookingDto.getEndBookingTime());
        assertEquals(createBookingDto.getItemId(), itemId);
    }

    @Test
    void getBookingsForBooker() {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();

        List<CreateItemDto> items = createItemDtos(2);
        CreateItemDto item1 = items.getFirst();
        long itemId1 = itemService.createItem(item1, ownerId).getId();

        CreateItemDto item2 = items.getLast();
        long itemId2 = itemService.createItem(item2, ownerId).getId();

        CreateUserDto bookerCreate = usersCreate.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();
        List<CreateBookingDto> createBookingDtos = createBookingDtos(2);

        CreateBookingDto createBookingDto1 = createBookingDtos.getFirst();
        createBookingDto1.setItemId(itemId1);
        bookingService.createBooking(createBookingDto1, bookerId);

        CreateBookingDto createBookingDto2 = createBookingDtos.getLast();
        createBookingDto2.setItemId(itemId2);
        bookingService.createBooking(createBookingDto2, bookerId);

        long count = bookingService.getBookingsForBooker(BookingController.StateParam.ALL, bookerId).size();
        assertEquals(2, count);
    }

    @Test
    void getBookingsForOwner() {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();

        List<CreateItemDto> items = createItemDtos(2);
        CreateItemDto item1 = items.getFirst();
        long itemId1 = itemService.createItem(item1, ownerId).getId();

        CreateItemDto item2 = items.getLast();
        long itemId2 = itemService.createItem(item2, ownerId).getId();

        CreateUserDto bookerCreate = usersCreate.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();
        List<CreateBookingDto> createBookingDtos = createBookingDtos(2);

        CreateBookingDto createBookingDto1 = createBookingDtos.getFirst();
        createBookingDto1.setItemId(itemId1);
        bookingService.createBooking(createBookingDto1, bookerId);

        CreateBookingDto createBookingDto2 = createBookingDtos.getLast();
        createBookingDto2.setItemId(itemId2);
        bookingService.createBooking(createBookingDto2, bookerId);

        long count = bookingService.getBookingsForOwner(BookingController.StateParam.ALL, ownerId).size();
        assertEquals(2, count);
    }

    @Test
    void createBooking() {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();
        CreateItemDto itemCreate = createItemDtos(1).getFirst();
        long itemId = itemService.createItem(itemCreate, ownerId).getId();

        CreateUserDto bookerCreate = usersCreate.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();
        CreateBookingDto createBookingDto = createBookingDtos(1).getFirst();
        createBookingDto.setItemId(itemId);
        GetBookingDto getBookingDto = bookingService.createBooking(createBookingDto, bookerId);

        assertNotNull(getBookingDto.getId());
        assertEquals(createBookingDto.getStartBookingTime(), getBookingDto.getStartBookingTime());
        assertEquals(createBookingDto.getEndBookingTime(), getBookingDto.getEndBookingTime());
        assertEquals(createBookingDto.getItemId(), itemId);
    }

    @Test
    void makeDecisionForBooking() {
        List<CreateUserDto> users = createUserDtos(2);
        CreateUserDto ownerCreate = users.getFirst();
        GetUserDto owner = userService.createUser(ownerCreate);
        CreateItemDto itemCreate = createItemDtos(1).getFirst();
        long itemId = itemService.createItem(itemCreate, owner.getId()).getId();

        CreateUserDto bookerCreate = users.getLast();
        GetUserDto booker = userService.createUser(bookerCreate);
        CreateBookingDto createBookingDto = createBookingDtos(1).getFirst();
        createBookingDto.setItemId(itemId);
        GetBookingDto bookingDto = bookingService.createBooking(createBookingDto, booker.getId());

        assertEquals(StatusBooking.WAITING.name(), bookingDto.getStatus().name());
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingDto.getId());

        assertTrue(bookingOptional.isPresent());
        assertEquals(StatusBooking.WAITING.name(), bookingOptional.get().getStatus().name());

        bookingDto = bookingService.makeDecisionForBooking(bookingDto.getId(), true, owner.getId());
        assertEquals(StatusBooking.APPROVED.name(), bookingDto.getStatus().name());

        bookingOptional = bookingRepository.findById(bookingDto.getId());
        assertTrue(bookingOptional.isPresent());
        assertEquals(StatusBooking.APPROVED.name(), bookingOptional.get().getStatus().name());
    }
}