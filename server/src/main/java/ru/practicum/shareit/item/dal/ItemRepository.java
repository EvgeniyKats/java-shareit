package ru.practicum.shareit.item.dal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId, Pageable page);

    List<Item> findByAvailableTrueAndNameContainingIgnoreCase(String nameContaining, Pageable page);

    void deleteByOwnerId(Long ownerId);
}
