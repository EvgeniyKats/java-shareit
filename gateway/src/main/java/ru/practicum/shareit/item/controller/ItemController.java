package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static ru.practicum.shareit.HttpHeaderNames.HEADER_USER_ID;
import static ru.practicum.shareit.PageConfig.DEFAULT_SIZE;

@Slf4j
@Validated
@RequiredArgsConstructor
@Controller
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public HttpEntity<Object> getItemById(@Min(1) @PathVariable Long itemId,
                                          @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public HttpEntity<Object> getItemsByUserId(@Min(1) @RequestHeader(HEADER_USER_ID) Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = DEFAULT_SIZE) Integer size) {
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public HttpEntity<Object> searchByText(@RequestParam String text,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = DEFAULT_SIZE) Integer size,
                                           @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return itemClient.getItemsByText(text, userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<Object> createItem(@Valid @RequestBody CreateItemDto createItemDto,
                                         @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return itemClient.createItem(createItemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<Object> commentItem(@Valid @RequestBody CreateCommentDto createCommentDto,
                                          @PathVariable Long itemId,
                                          @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return itemClient.commentItem(createCommentDto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public HttpEntity<Object> updateItem(@Min(1) @PathVariable Long itemId,
                                         @Valid @RequestBody UpdateItemDto updateItemDto,
                                         @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        return itemClient.updateItem(updateItemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@Min(1) @PathVariable Long itemId,
                           @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        itemClient.deleteItemById(itemId, userId);
    }
}
