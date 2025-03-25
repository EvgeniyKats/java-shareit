package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.custom.BadRequestException;
import ru.practicum.shareit.exception.custom.NotFoundException;
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
    public Item getItemById(Long itemId, Long userId) {
        log.trace("Попытка получить предмет с itemId = {}, от пользователя userId = {}", itemId, userId);
        throwNotFoundIfUserAbsent(userId);
        return getItemByIdOrThrowNotFound(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        log.trace("Попытка получить предметы пользователя userId = {}", userId);
        throwNotFoundIfUserAbsent(userId);
        return itemRepository.getItemsByUserId(userId);
    }

    @Override
    public List<Item> getItemsByText(String text, Long userId) {
        log.trace("Попытка получить предметы через поиск \"{}\" от пользователя userId = {}", text, userId);
        throwNotFoundIfUserAbsent(userId);
        return itemRepository.getItemsByText(text);
    }

    @Override
    public Item createItem(Item item, Long userId) {
        log.trace("Попытка создать предмет от пользователя userId = {}", userId);
        throwNotFoundIfUserAbsent(userId);
        itemRepository.createItem(item, userId);
        log.trace("Предмет успешно создан пользователем userId = {}, его itemId = {}", userId, item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item updatedItem, Long itemId, Long userId) {
        log.trace("Попытка создать предмет itemId = {}, от пользователя userId = {}", itemId, userId);
        throwNotFoundIfUserAbsent(userId);

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

        return itemRepository.updateItem(updatedItem, itemId, userId, needToUpdateName);
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
