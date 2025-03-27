package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> itemByItemId = new HashMap<>();
    private final Map<Long, Set<Item>> itemsByUserId = new HashMap<>();
    /**
     * Хранит соответствие: каждое слово имени предмета (item.name.split) : уникальные предметы (Item)
     * Полезно, если чтение чаще модификации
     */
    private final Map<String, Set<Item>> itemsByPartOfName = new HashMap<>();
    private Long idItemNext = 1L;

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(itemByItemId.get(itemId));
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        return new ArrayList<>(itemsByUserId.get(userId));
    }

    /**
     * @param text Запрос на поиск, по каждому слову которого будет произведён суммарный поиск
     * @return Список уникальных предметов, доступных для бронирования
     */
    public List<Item> getItemsByText(String text) {
        Set<Item> ans = new HashSet<>();
        String[] words = text.split(" ");

        // Получение совпадений из мапы, по каждому слову из запроса
        for (String w : words) {
            Set<Item> items = itemsByPartOfName.get(w);
            if (items == null) continue;
            for (Item item : items) {
                if (item.getIsAvailable()) {
                    ans.add(item);
                }
            }
        }

        return new ArrayList<>(ans);
    }

    @Override
    public Item createItem(Item item, Long userId) {
        item.setOwner(userId);
        item.setId(idItemNext++);
        String[] partOfName = item.getName().split(" ");

        itemByItemId.put(item.getId(), item);

        itemsByUserId.putIfAbsent(userId, new HashSet<>());
        itemsByUserId.get(userId).add(item);

        // Запись соответсвий
        for (String part : partOfName) {
            part = part.toUpperCase();
            itemsByPartOfName.putIfAbsent(part, new HashSet<>());
            itemsByPartOfName.get(part).add(item);
        }

        return item;
    }

    @Override
    public Item updateItem(Item updatedItem, Long itemId, Long userId, boolean needToUpdateName) {
        Item currentItem = itemByItemId.get(itemId);

        if (needToUpdateName) {
            String[] oldParts = currentItem.getName().split(" ");
            String[] newParts = updatedItem.getName().split(" ");

            // Удаление старого соответствия
            for (String part : oldParts) {
                part = part.toUpperCase();
                itemsByPartOfName.get(part).remove(currentItem);
            }

            // Запись нового соответствия
            for (String part : newParts) {
                part = part.toUpperCase();
                itemsByPartOfName.putIfAbsent(part, new HashSet<>());
                itemsByPartOfName.get(part).add(currentItem);
            }
        }

        return currentItem.updateFromAnotherItem(updatedItem);
    }

    @Override
    public boolean deleteItemById(Long itemId, Long userId) {
        Item item = itemByItemId.remove(itemId);
        if (item == null) return false;
        itemsByUserId.remove(userId);

        String[] parts = item.getName().split(" ");

        // Удаление соответствия
        for (String part : parts) {
            part = part.toUpperCase();
            itemsByPartOfName.get(part).remove(item);
        }

        return true;
    }

    @Override
    public void deleteAllItemsByUserId(Long userId) {
        Set<Item> items = itemsByUserId.remove(userId);
        if (items == null) return;

        for (Item item : items) {
            itemByItemId.remove(item.getId());

            String[] parts = item.getName().split(" ");

            // Удаление соответствия
            for (String part : parts) {
                part = part.toUpperCase();
                itemsByPartOfName.get(part).remove(item);
            }
        }
    }
}
