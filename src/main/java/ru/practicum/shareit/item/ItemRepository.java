package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getItemById(Long itemId);

    List<Item> getItemsByUserId(Long userId);

    List<Item> getItemsByText(String text);

    Item createItem(Item item, Long userId);

    Item updateItem(Item updatedItem, Long itemId, Long userId, boolean needToUpdateName);

    boolean deleteItemById(Long itemId, Long userId);

    void deleteAllItemsByUserId(Long userId);
}
