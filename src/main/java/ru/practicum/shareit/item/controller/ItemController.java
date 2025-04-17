package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    public static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public GetItemDto getItemById(@Min(1) @PathVariable Long itemId,
                                  @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен GET /items/{} , userId = {}", itemId, userId);
        GetItemDto ans = itemService.getItemById(itemId, userId);
        log.info("Предмет с itemId = {}, успешно получен", itemId);
        return ans;
    }

    @GetMapping
    public List<GetItemDto> getItemsByUserId(@Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен GET /items, userId = {}", userId);
        List<GetItemDto> ans = itemService.getItemsByUserId(userId);
        log.info("Список предметов пользователя userId = {} : items = {}", userId, ans);
        return ans;
    }

    @GetMapping("/search")
    public List<GetItemDto> searchByText(@RequestParam String text,
                                         @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен GET /items/search?text={}, userId={}", text, userId);
        List<GetItemDto> ans = itemService.getItemsByText(text, userId);
        log.info("Список предметов по запросу GET /items/search?text={} : items = {}", text, ans);
        return ans;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GetItemDto createItem(@Valid @RequestBody CreateItemDto createItemDto,
                                 @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен POST /items, userId = {}", userId);
        GetItemDto ans = itemService.createItem(createItemDto, userId);
        log.info("Предмет, успешно создан, его itemId = {}", ans.getId());
        return ans;
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public GetCommentDto commentItem(@Valid @RequestBody CreateCommentDto createCommentDto,
                                     @PathVariable Long itemId,
                                     @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен POST /items/{}/comment, userId = {}, text = {}", itemId, userId, createCommentDto.getText());
        GetCommentDto ans = itemService.commentItem(createCommentDto, userId, itemId);
        log.info("Комментарий успешно добавлен для предмета {}", itemId);
        return ans;
    }

    @PatchMapping("/{itemId}")
    public GetItemDto updateItem(@Min(1) @PathVariable Long itemId,
                                 @Valid @RequestBody UpdateItemDto updateItemDto,
                                 @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен PATCH /items/{} , userId = {}", itemId, userId);
        GetItemDto ans = itemService.updateItem(updateItemDto, itemId, userId);
        log.info("Предмет с itemId = {}, успешно обновлен, его параметры owner = {}, name = {}, description = {}, "
                        + "available = {}",
                itemId,
                ans.getOwnerId(),
                ans.getName(),
                ans.getDescription(),
                ans.getAvailable());
        return ans;
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@Min(1) @PathVariable Long itemId,
                           @Min(1) @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен DELETE /items/{} , userId = {}", itemId, userId);
        itemService.deleteItemById(itemId, userId);
        log.info("Предмет с itemId = {}, успешно удалён", itemId);
    }
}
