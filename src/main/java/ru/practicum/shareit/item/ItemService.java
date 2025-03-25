package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(Long itemId, Long userId);

    List<Item> getItemsByUserId(Long userId);

    List<Item> getItemsByText(String text, Long userId);

    Item createItem(Item item, Long userId);

    Item updateItem(Item updatedItem, Long itemId, Long userId);

    void deleteItemById(Long itemId, Long userId);
}
