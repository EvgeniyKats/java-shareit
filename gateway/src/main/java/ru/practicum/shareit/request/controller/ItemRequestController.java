package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

@Validated
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public HttpEntity<Object> getUserItemRequests(
            @Min(1) @RequestHeader(ItemController.HEADER_USER_ID) Long userId) {
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public HttpEntity<Object> getAllItemRequests(
            @Min(1) @RequestHeader(ItemController.HEADER_USER_ID) Long userId) {
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public HttpEntity<Object> getItemRequestById(@Min(1) @RequestHeader(ItemController.HEADER_USER_ID) Long userId,
                                                 @Min(1) @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<Object> createItemRequest(@Min(1) @RequestHeader(ItemController.HEADER_USER_ID) Long userId,
                                                @Valid @RequestBody CreateItemRequestDto createItemRequestDto) {
        return itemRequestClient.createItemRequest(userId, createItemRequestDto);
    }
}
