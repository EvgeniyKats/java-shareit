package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<GetItemRequestDto> getUserItemRequests(Long userId);

    List<GetItemRequestDto> getAllItemRequests(Long userId);

    GetItemRequestDto getItemRequestById(Long userId, Long requestId);

    GetItemRequestDto createItemRequest(Long userId, CreateItemRequestDto createItemRequestDto);
}
