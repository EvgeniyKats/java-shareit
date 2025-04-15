package ru.practicum.shareit.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @OneToMany(mappedBy = "item")
    private List<Comment> comments = new ArrayList<>();

    @Transient
    private Booking lastBooking;

    @Transient
    private Booking nextBooking;

    public void updateFromAnotherItem(Item updatedItem) {
        if (updatedItem.hasName()) name = updatedItem.getName();
        if (updatedItem.hasDescription()) description = updatedItem.getDescription();
        if (updatedItem.hasAvailable()) available = updatedItem.getAvailable();
    }

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasAvailable() {
        return available != null;
    }
}

