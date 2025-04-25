package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<GetItemRequestDto> getUserItemRequests(Long userId, Integer from, Integer size);

    List<GetItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size);

    GetItemRequestDto getItemRequestById(Long userId, Long requestId);

    GetItemRequestDto createItemRequest(Long userId, CreateItemRequestDto createItemRequestDto);
}
