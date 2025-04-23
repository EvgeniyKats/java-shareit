package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<GetItemRequestDto> getUserItemRequests(
            @Min(1) @RequestHeader(ItemController.HEADER_USER_ID) Long userId) {
        log.info("Получен GET запрос /requests , userId = {}", userId);
        List<GetItemRequestDto> ans = itemRequestService.getUserItemRequests(userId);
        log.info("У пользователя userId = {} найдено запросов = {}", userId, ans.size());
        return ans;
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> getAllItemRequests(
            @Min(1) @RequestHeader(ItemController.HEADER_USER_ID) Long userId) {
        log.info("Получен GET запрос /requests/all , userId = {}", userId);
        List<GetItemRequestDto> ans = itemRequestService.getAllItemRequests(userId);
        log.info("Найдено всего запросов = {}", ans.size());
        return ans;
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto getItemRequestById(@Min(1) @RequestHeader(ItemController.HEADER_USER_ID) Long userId,
                                                @Min(1) @PathVariable Long requestId) {
        log.info("Получен GET запрос /requests/{} , userId = {}", requestId, userId);
        GetItemRequestDto ans = itemRequestService.getItemRequestById(userId, requestId);
        log.info("Найден itemRequest = {}", ans.toString());
        return ans;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GetItemRequestDto createItemRequest(@Min(1) @RequestHeader(ItemController.HEADER_USER_ID) Long userId,
                                               @Valid @RequestBody CreateItemRequestDto createItemRequestDto) {
        log.info("Получен POST запрос /requests , userId = {}, createDto = {}",
                userId, createItemRequestDto.toString());
        GetItemRequestDto ans = itemRequestService.createItemRequest(userId, createItemRequestDto);
        log.info("Запрос успешно создан пользователем , userId = {}", userId);
        return ans;
    }
}
