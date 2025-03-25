package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.MapperItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public GetItemDto getItemById(@PathVariable Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET /items/{} , userId = {}", itemId, userId);
        Item item = itemService.getItemById(itemId, userId);
        log.info("Предмет с itemId = {}, успешно получен", itemId);
        return MapperItemDto.itemToGetDto(item);
    }

    @GetMapping
    public List<GetItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET /items, userId = {}", userId);
        List<Item> items = itemService.getItemsByUserId(userId);
        log.info("Список предметов пользователя userId = {} : items = {}", userId, items);
        return items.stream().map(MapperItemDto::itemToGetDto).toList();
    }

    @GetMapping("/search")
    public List<GetItemDto> searchByText(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET /items/search?text={}", text);
        List<Item> items = itemService.getItemsByText(text, userId);
        log.info("Список предметов по запросу GET /items/search?text={} : items = {}", text, items);
        return items.stream().map(MapperItemDto::itemToGetDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GetItemDto createItem(@Valid @RequestBody CreateItemDto createItemDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен POST /items, userId = {}", userId);
        Item item = itemService.createItem(MapperItemDto.createDtoToItem(createItemDto), userId);
        log.info("Предмет, успешно создан, его itemId = {}", item.getId());
        return MapperItemDto.itemToGetDto(item);
    }

    @PatchMapping("/{itemId}")
    public GetItemDto updateItem(@PathVariable Long itemId,
                                 @Valid @RequestBody UpdateItemDto updateItemDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен PATCH /items/{} , userId = {}", itemId, userId);
        Item item = itemService.updateItem(MapperItemDto.updateDtoToItem(updateItemDto), itemId, userId);
        log.info("Предмет с itemId = {}, успешно обновлен, его параметры owner = {}, name = {}, description = {}, "
                        + "available = {}",
                itemId,
                item.getOwner(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable());
        return MapperItemDto.itemToGetDto(item);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен DELETE /items/{} , userId = {}", itemId, userId);
        itemService.deleteItemById(itemId, userId);
        log.info("Предмет с itemId = {}, успешно удалён", itemId);
    }
}
