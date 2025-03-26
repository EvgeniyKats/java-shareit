package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.custom.BadRequestException;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.MapperItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImplement implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public GetItemDto getItemById(Long itemId, Long userId) {
        log.trace("Попытка получить предмет с itemId = {}, от пользователя userId = {}", itemId, userId);
        throwNotFoundIfUserAbsent(userId);
        Item item = getItemByIdOrThrowNotFound(itemId);
        return MapperItemDto.itemToGetDto(item);
    }

    @Override
    public List<GetItemDto> getItemsByUserId(Long userId) {
        log.trace("Попытка получить предметы пользователя userId = {}", userId);
        throwNotFoundIfUserAbsent(userId);
        List<Item> items = itemRepository.getItemsByUserId(userId);
        return items.stream()
                .map(MapperItemDto::itemToGetDto)
                .toList();
    }

    @Override
    public List<GetItemDto> getItemsByText(String text, Long userId) {
        log.trace("Попытка получить предметы через поиск \"{}\" от пользователя userId = {}", text, userId);
        throwNotFoundIfUserAbsent(userId);
        List<Item> items = itemRepository.getItemsByText(text);
        return items.stream()
                .map(MapperItemDto::itemToGetDto)
                .toList();
    }

    @Override
    public GetItemDto createItem(CreateItemDto createItemDto, Long userId) {
        log.trace("Попытка создать предмет от пользователя userId = {}", userId);
        throwNotFoundIfUserAbsent(userId);
        Item item = MapperItemDto.createDtoToItem(createItemDto);
        itemRepository.createItem(item, userId);
        log.trace("Предмет успешно создан пользователем userId = {}, его itemId = {}", userId, item.getId());
        return MapperItemDto.itemToGetDto(item);
    }

    @Override
    public GetItemDto updateItem(UpdateItemDto updateItemDto, Long itemId, Long userId) {
        log.trace("Попытка создать предмет itemId = {}, от пользователя userId = {}", itemId, userId);
        throwNotFoundIfUserAbsent(userId);
        Item updatedItem = MapperItemDto.updateDtoToItem(updateItemDto);

        Item currentItem = getItemByIdOrThrowNotFound(itemId);
        throwBadRequestIfUserNotOwnerOfItem(currentItem, userId);

        boolean needToUpdateName;

        log.trace("Проверка необходимости обновления имени у предмета itemId = {}", itemId);
        if (updatedItem.hasName() && !updatedItem.getName().equals(currentItem.getName())) {
            log.trace("У предмета с itemId = {}, необходимо обновить имя", itemId);
            needToUpdateName = true;
        } else {
            log.trace("У предмета с itemId = {}, не меняется имя", itemId);
            needToUpdateName = false;
        }
        Item ans = itemRepository.updateItem(updatedItem, itemId, userId, needToUpdateName);
        log.trace("Предмет с itemId = {}, успешно обновлен", itemId);
        return MapperItemDto.itemToGetDto(ans);
    }

    @Override
    public void deleteItemById(Long itemId, Long userId) {
        log.trace("Попытка удалить предмет itemId = {}, от пользователя userId = {}", itemId, userId);
        throwNotFoundIfUserAbsent(userId);
        Item currentItem = getItemByIdOrThrowNotFound(itemId);
        throwBadRequestIfUserNotOwnerOfItem(currentItem, userId);

        if (!itemRepository.deleteItemById(itemId, userId)) {
            throw NotFoundException.builder()
                    .setNameObject("Предмет")
                    .setNameParameter("itemId")
                    .setValueParameter(itemId)
                    .build();
        }
        log.trace("Успешно удален предмет с itemId = {}", itemId);
    }

    private Item getItemByIdOrThrowNotFound(Long itemId) {
        log.trace("Проверка предмета с itemId = {} в хранилище", itemId);
        Optional<Item> optionalItem = itemRepository.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            throw NotFoundException.builder()
                    .setNameObject("Предмет")
                    .setNameParameter("itemId")
                    .setValueParameter(itemId)
                    .build();
        }
        log.trace("Предмет с itemId = {}, найден", itemId);
        return optionalItem.get();
    }

    private void throwNotFoundIfUserAbsent(Long userId) {
        log.trace("Проверка на существования пользователя с userId = {}", userId);
        if (userRepository.getUserById(userId).isEmpty()) {
            throw NotFoundException.builder()
                    .setNameObject("Пользователь")
                    .setNameParameter("userId")
                    .setValueParameter(userId)
                    .build();
        }
        log.trace("Пользователь с userId = {}, найден", userId);
    }

    private void throwBadRequestIfUserNotOwnerOfItem(Item item, Long userId) {
        log.trace("Проверка на соответствие владельца предмета itemId = {} и пользователя userId = {}",
                item.getId(),
                userId);
        if (!item.getOwner().equals(userId)) {
            throw new BadRequestException("Пользователь с userId = " + userId
                    + " не владеет предметом itemId = " + item.getId());
        }
        log.trace("Владение предметом itemId = {} подтверждено", item.getId());
    }
}
