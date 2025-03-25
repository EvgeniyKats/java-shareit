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
     * Хранит соответствие каждого слова (разделенного пробелом) item.name : item
     * Полезно, если получение происходит чаще изменения названия предмета
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
     * @param text Искомый текст по имени предмета, который разобьётся на String[] word
     * @return Список уникальных предметов, доступных для бронирования
     */
    public List<Item> getItemsByText(String text) {
        Set<Item> ans = new HashSet<>();
        String[] words = text.split(" ");

        // Получение из мапы совпадений по каждому слову запроса
        for (String w : words) {
            if (itemsByPartOfName.get(w) == null) continue;
            for (Item item : itemsByPartOfName.get(w)) {
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

        // Запись в мапу соответсвие каждого слова из названия и предмета
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
            itemsByPartOfName.get(part).remove(item);
        }

        return true;
    }
}
