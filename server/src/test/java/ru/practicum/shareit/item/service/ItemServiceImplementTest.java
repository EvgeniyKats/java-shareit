package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.UtilTest.NAME_BASE;
import static ru.practicum.shareit.UtilTest.createCommentDtos;
import static ru.practicum.shareit.UtilTest.createItemDtos;
import static ru.practicum.shareit.UtilTest.createUserDtos;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplementTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void getItemById() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        List<CreateItemDto> itemsCreate = createItemDtos(1);
        CreateItemDto itemCreate = itemsCreate.getFirst();
        long itemId = itemService.createItem(itemCreate, userId).getId();

        GetItemDto getItemDto = itemService.getItemById(itemId, userId);

        assertNotNull(getItemDto.getId());
        assertEquals(itemCreate.getName(), getItemDto.getName());
        assertEquals(itemCreate.getDescription(), getItemDto.getDescription());
        assertEquals(itemCreate.getAvailable(), getItemDto.getAvailable());
        assertEquals(itemCreate.getRequestId(), getItemDto.getRequestId());
    }

    @Test
    void getItemsByUserId() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        List<CreateItemDto> itemsCreate = createItemDtos(2);
        CreateItemDto itemCreate1 = itemsCreate.getFirst();
        long itemId1 = itemService.createItem(itemCreate1, userId).getId();

        CreateItemDto itemCreate2 = itemsCreate.getLast();
        long itemId2 = itemService.createItem(itemCreate2, userId).getId();

        List<GetItemDto> items = itemService.getItemsByUserId(userId);
        assertEquals(2, items.size());
        List<Long> ids = items.stream()
                .map(GetItemDto::getId)
                .toList();
        assertTrue(ids.contains(itemId1));
        assertTrue(ids.contains(itemId2));
    }

    @Test
    void getItemsByText() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        List<CreateItemDto> itemsCreate = createItemDtos(2);
        CreateItemDto itemCreate1 = itemsCreate.getFirst();
        long itemId1 = itemService.createItem(itemCreate1, userId).getId();

        CreateItemDto itemCreate2 = itemsCreate.getLast();
        long itemId2 = itemService.createItem(itemCreate2, userId).getId();

        List<GetItemDto> items = itemService.getItemsByText(NAME_BASE, userId);
        assertEquals(2, items.size());
        List<Long> ids = items.stream()
                .map(GetItemDto::getId)
                .toList();
        assertTrue(ids.contains(itemId1));
        assertTrue(ids.contains(itemId2));
    }

    @Test
    void createItem() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        List<CreateItemDto> itemsCreate = createItemDtos(1);
        CreateItemDto itemCreate = itemsCreate.getFirst();
        GetItemDto getItemDto = itemService.createItem(itemCreate, userId);

        assertNotNull(getItemDto.getId());
        assertEquals(itemCreate.getName(), getItemDto.getName());
        assertEquals(itemCreate.getDescription(), getItemDto.getDescription());
        assertEquals(itemCreate.getAvailable(), getItemDto.getAvailable());
        assertEquals(itemCreate.getRequestId(), getItemDto.getRequestId());
    }

    @Test
    void updateItem() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        List<CreateItemDto> itemsCreate = createItemDtos(1);
        CreateItemDto itemCreate = itemsCreate.getFirst();
        long itemId = itemService.createItem(itemCreate, userId).getId();

        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setDescription("newDescription");
        updateItemDto.setName("newName");
        updateItemDto.setAvailable(false);

        GetItemDto getItemDto = itemService.updateItem(updateItemDto, itemId, userId);
        assertEquals(updateItemDto.getDescription(), getItemDto.getDescription());
        assertEquals(updateItemDto.getName(), getItemDto.getName());
        assertEquals(updateItemDto.getAvailable(), getItemDto.getAvailable());
    }

    @Test
    void deleteItemById() {
        List<CreateUserDto> usersCreate = createUserDtos(1);
        CreateUserDto userCreate = usersCreate.getFirst();
        long userId = userService.createUser(userCreate).getId();

        List<CreateItemDto> itemsCreate = createItemDtos(1);
        CreateItemDto itemCreate = itemsCreate.getFirst();
        long itemId = itemService.createItem(itemCreate, userId).getId();

        itemService.deleteItemById(itemId, userId);
        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId, userId));
            }

    @Test
    @Transactional(isolation = Isolation.SERIALIZABLE)
    void commentItem() throws InterruptedException {
        List<CreateUserDto> usersCreate = createUserDtos(2);
        CreateUserDto ownerCreate = usersCreate.getFirst();
        long ownerId = userService.createUser(ownerCreate).getId();

        CreateUserDto bookerCreate = usersCreate.getLast();
        long bookerId = userService.createUser(bookerCreate).getId();

        List<CreateItemDto> itemsCreate = createItemDtos(1);
        CreateItemDto itemCreate = itemsCreate.getFirst();
        long itemId = itemService.createItem(itemCreate, ownerId).getId();

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(itemId);
        createBookingDto.setStartBookingTime(LocalDateTime.now().plusSeconds(1));
        createBookingDto.setEndBookingTime(LocalDateTime.now().plusSeconds(2));
        long bookingId = bookingService.createBooking(createBookingDto, bookerId).getId();
        bookingService.makeDecisionForBooking(bookingId, true, ownerId);
        Thread.sleep(3000);

        List<CreateCommentDto> commentsCreate = createCommentDtos(1);
        CreateCommentDto commentCreate = commentsCreate.getFirst();
        GetCommentDto commentDto = itemService.commentItem(commentCreate, bookerId, itemId);
        assertEquals(commentCreate.getCreatedTime(), commentDto.getCreatedTime());
        assertEquals(commentCreate.getText(), commentDto.getText());
    }
}