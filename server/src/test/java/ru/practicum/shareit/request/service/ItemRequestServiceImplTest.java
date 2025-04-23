package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.UtilTest;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.UtilTest.DESCRIPTION_BASE;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Test
    void getUserItemRequests() {
        User user = UtilTest.createUsers(1).getFirst();
        userRepository.save(user);

        List<CreateItemRequestDto> requestDtos = UtilTest.createItemRequestsDtos(3);

        requestDtos.forEach(dto -> itemRequestService.createItemRequest(user.getId(), dto));

        List<GetItemRequestDto> userRequests = itemRequestService.getUserItemRequests(user.getId());

        assertEquals(3, userRequests.size());
    }

    @Test
    void getAllItemRequests() {
        List<User> users = UtilTest.createUsers(2);

        User user1 = users.getFirst();
        userRepository.save(user1);
        List<CreateItemRequestDto> requestDtos1 = UtilTest.createItemRequestsDtos(3);
        requestDtos1.forEach(dto -> itemRequestService.createItemRequest(user1.getId(), dto));

        User user2 = users.getLast();
        userRepository.save(user2);
        List<CreateItemRequestDto> requestDtos2 = UtilTest.createItemRequestsDtos(3);
        requestDtos2.forEach(dto -> itemRequestService.createItemRequest(user1.getId(), dto));

        List<GetItemRequestDto> allRequests = itemRequestService.getAllItemRequests(user1.getId());

        assertEquals(6, allRequests.size());
    }

    @Test
    void getItemRequestById() {
        User user = UtilTest.createUsers(1).getFirst();
        userRepository.save(user);

        CreateItemRequestDto createItemRequestDto = UtilTest.createItemRequestsDtos(1).getFirst();

        Long itemId = itemRequestService.createItemRequest(user.getId(), createItemRequestDto).getId();

        GetItemRequestDto itemRequest = itemRequestService.getItemRequestById(user.getId(), itemId);

        assertNotNull(itemRequest.getId());
        assertNotNull(itemRequest.getItems());
        assertEquals(user.getId(), itemRequest.getOwnerId());
        assertEquals(DESCRIPTION_BASE + 1, itemRequest.getDescription());
    }

    @Test
    void createItemRequest() {
        User user = UtilTest.createUsers(1).getFirst();
        userRepository.save(user);
        CreateItemRequestDto createItemRequestDto = UtilTest.createItemRequestsDtos(1).getFirst();

        itemRequestService.createItemRequest(user.getId(), createItemRequestDto);

        List<ItemRequest> requests = requestRepository.findAll();
        assertEquals(1, requests.size());

        ItemRequest itemRequest = requests.getFirst();

        assertNotNull(itemRequest.getId());
        assertNotNull(itemRequest.getItems());
        assertEquals(user.getId(), itemRequest.getOwnerId());
        assertEquals(DESCRIPTION_BASE + 1, itemRequest.getDescription());
    }

    @Test
    void getUserItemRequestsNotFound() {
        User user = UtilTest.createUsers(1).getFirst();
        userRepository.save(user);
        CreateItemRequestDto dto = UtilTest.createItemRequestsDtos(1).getFirst();
        itemRequestService.createItemRequest(user.getId(), dto);
        assertThrows(NotFoundException.class, () -> itemRequestService.getUserItemRequests(-1L));
    }


    @Test
    void getAllItemRequestsNotFound() {
        User user = UtilTest.createUsers(1).getFirst();
        userRepository.save(user);
        CreateItemRequestDto dto = UtilTest.createItemRequestsDtos(1).getFirst();

        itemRequestService.createItemRequest(user.getId(), dto);
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllItemRequests(-1L));
    }

    @Test
    void getItemRequestByIdNotFound() {
        User user = UtilTest.createUsers(1).getFirst();
        userRepository.save(user);
        CreateItemRequestDto dto = UtilTest.createItemRequestsDtos(1).getFirst();
        long itemId = itemRequestService.createItemRequest(user.getId(), dto).getId();
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(-1L, itemId));
    }

    @Test
    void getItemRequestByIdNotFoundBadRequestId() {
        User user = UtilTest.createUsers(1).getFirst();
        userRepository.save(user);
        CreateItemRequestDto dto = UtilTest.createItemRequestsDtos(1).getFirst();
        long itemId = itemRequestService.createItemRequest(user.getId(), dto).getId();
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(user.getId(), -1L));
    }


    @Test
    void createItemRequestNotFound() {
        User user = UtilTest.createUsers(1).getFirst();
        userRepository.save(user);
        CreateItemRequestDto dto = UtilTest.createItemRequestsDtos(1).getFirst();
        itemRequestService.createItemRequest(user.getId(), dto);
        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(-1L, dto));
    }
}