package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Item {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Boolean isAvailable;

    public Item updateFromAnotherItem(Item updatedItem) {
        if (updatedItem.hasName()) name = updatedItem.getName();
        if (updatedItem.hasDescription()) description = updatedItem.getDescription();
        if (updatedItem.hasAvailable()) isAvailable = updatedItem.getIsAvailable();
        return this;
    }

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasAvailable() {
        return isAvailable != null;
    }
}

