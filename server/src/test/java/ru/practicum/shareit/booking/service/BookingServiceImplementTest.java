package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.exception.custom.BadRequestException;
import ru.practicum.shareit.exception.custom.ForbiddenException;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.PageConfig.DEFAULT_SIZE_INT;
import static ru.practicum.shareit.booking.controller.BookingController.StateParam;

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

        long count = bookingService.getBookingsForBooker(StateParam.ALL, bookerId, 0, DEFAULT_SIZE_INT).size();
        assertEquals(2, count);
    }

    @Test
    void getBookingsStatus() throws InterruptedException {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        //owner
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();

        //booker
        CreateUserDto bookerCreate = usersCreate.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();

        List<CreateBookingDto> createBookingDtos = createBookingDtos(2);

        // Подготовка
        List<CreateItemDto> items = createItemDtos(2);

        CreateItemDto itemAccept = items.getFirst();
        long itemIdAccept = itemService.createItem(itemAccept, ownerId).getId();
        CreateBookingDto dtoAccept = createBookingDtos.get(0);
        dtoAccept.setStartBookingTime(LocalDateTime.now().plusSeconds(2));
        dtoAccept.setEndBookingTime(LocalDateTime.now().plusSeconds(6));
        dtoAccept.setItemId(itemIdAccept);

        // WAITING
        assertEquals(0, bookingService.getBookingsForBooker(StateParam.WAITING, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(0, bookingService.getBookingsForOwner(StateParam.WAITING, ownerId, 0, DEFAULT_SIZE_INT).size());
        long acceptId = bookingService.createBooking(dtoAccept, bookerId).getId();
        assertEquals(1, bookingService.getBookingsForBooker(StateParam.WAITING, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(1, bookingService.getBookingsForOwner(StateParam.WAITING, ownerId, 0, DEFAULT_SIZE_INT).size());
        // FUTURE
        assertEquals(0, bookingService.getBookingsForBooker(StateParam.FUTURE, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(0, bookingService.getBookingsForOwner(StateParam.FUTURE, ownerId, 0, DEFAULT_SIZE_INT).size());
        bookingService.makeDecisionForBooking(acceptId, true, ownerId);
        assertEquals(1, bookingService.getBookingsForBooker(StateParam.FUTURE, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(1, bookingService.getBookingsForOwner(StateParam.FUTURE, ownerId, 0, DEFAULT_SIZE_INT).size());
        // CURRENT
        assertEquals(0, bookingService.getBookingsForBooker(StateParam.CURRENT, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(0, bookingService.getBookingsForOwner(StateParam.CURRENT, ownerId, 0, DEFAULT_SIZE_INT).size());

        Thread.sleep(3000);
        assertEquals(1, bookingService.getBookingsForBooker(StateParam.CURRENT, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(1, bookingService.getBookingsForOwner(StateParam.CURRENT, ownerId, 0, DEFAULT_SIZE_INT).size());
        // PAST
        assertEquals(0, bookingService.getBookingsForBooker(StateParam.PAST, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(0, bookingService.getBookingsForOwner(StateParam.PAST, ownerId, 0, DEFAULT_SIZE_INT).size());
        Thread.sleep(4000);
        assertEquals(1, bookingService.getBookingsForBooker(StateParam.PAST, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(1, bookingService.getBookingsForOwner(StateParam.PAST, ownerId, 0, DEFAULT_SIZE_INT).size());

        CreateItemDto itemReject = items.getFirst();
        long itemIdReject = itemService.createItem(itemReject, ownerId).getId();
        CreateBookingDto dtoReject = createBookingDtos.get(1);
        dtoReject.setStartBookingTime(LocalDateTime.now().plusSeconds(2));
        dtoReject.setEndBookingTime(LocalDateTime.now().plusSeconds(6));
        dtoReject.setItemId(itemIdReject);
        long itemRejectId = bookingService.createBooking(dtoReject, bookerId).getId();

        // REJECT
        assertEquals(0, bookingService.getBookingsForBooker(StateParam.REJECTED, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(0, bookingService.getBookingsForOwner(StateParam.REJECTED, ownerId, 0, DEFAULT_SIZE_INT).size());
        bookingService.makeDecisionForBooking(itemRejectId, false, ownerId);
        assertEquals(1, bookingService.getBookingsForBooker(StateParam.REJECTED, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(1, bookingService.getBookingsForOwner(StateParam.REJECTED, ownerId, 0, DEFAULT_SIZE_INT).size());

        // ALL
        assertEquals(2, bookingService.getBookingsForBooker(StateParam.ALL, bookerId, 0, DEFAULT_SIZE_INT).size());
        assertEquals(2, bookingService.getBookingsForOwner(StateParam.ALL, ownerId, 0, DEFAULT_SIZE_INT).size());
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

        long count = bookingService.getBookingsForOwner(StateParam.ALL, ownerId, 0, DEFAULT_SIZE_INT).size();
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

    @Test
    void makeDecisionForBookingByFakeOwner() {
        List<CreateUserDto> users = createUserDtos(2);
        CreateUserDto ownerCreate = users.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();
        CreateItemDto itemCreate = createItemDtos(1).getFirst();
        long itemId = itemService.createItem(itemCreate, ownerId).getId();

        CreateUserDto bookerCreate = users.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();
        CreateBookingDto createBookingDto = createBookingDtos(1).getFirst();
        createBookingDto.setItemId(itemId);
        long bookingId = bookingService.createBooking(createBookingDto, bookerId).getId();

        assertThrows(ForbiddenException.class, () -> bookingService.makeDecisionForBooking(bookingId, true, bookerId));
    }

    @Test
    void testItemNotFound() {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();

        CreateItemDto itemCreate = createItemDtos(1).getFirst();
        long itemId = itemService.createItem(itemCreate, ownerId).getId();

        CreateUserDto bookerCreate = usersCreate.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();

        long fakeItemId = -1L;

        CreateBookingDto createBookingDto1 = createBookingDtos(1).getFirst();
        createBookingDto1.setItemId(fakeItemId);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(createBookingDto1, bookerId));
    }

    @Test
    void testUserNotFound() {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();

        CreateItemDto itemCreate = createItemDtos(1).getFirst();
        long itemId = itemService.createItem(itemCreate, ownerId).getId();

        CreateUserDto bookerCreate = usersCreate.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();

        long fakeBookerId = -1L;

        CreateBookingDto createBookingDto1 = createBookingDtos(1).getFirst();
        createBookingDto1.setItemId(itemId);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(createBookingDto1, fakeBookerId));
    }

    @Test
    void testBookingNotFound() {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();

        long fakeBookingId = -1L;

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(fakeBookingId, ownerId));
    }

    @Test
    void testOwnerWantBookItem() {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();

        CreateItemDto itemCreate = createItemDtos(1).getFirst();
        long itemId = itemService.createItem(itemCreate, ownerId).getId();

        CreateBookingDto createBookingDto1 = createBookingDtos(1).getFirst();
        createBookingDto1.setItemId(itemId);
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(createBookingDto1, ownerId));
    }
}