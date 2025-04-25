package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    GetItemDto getItemById(Long itemId, Long userId);

    List<GetItemDto> getItemsByUserId(Long userId, Integer from, Integer size);

    List<GetItemDto> getItemsByText(String text, Long userId, Integer from, Integer size);

    GetItemDto createItem(CreateItemDto createItemDto, Long userId);

    GetItemDto updateItem(UpdateItemDto updateItemDto, Long itemId, Long userId);

    void deleteItemById(Long itemId, Long userId);

    GetCommentDto commentItem(CreateCommentDto createCommentDto, Long userId, Long itemId);
}
