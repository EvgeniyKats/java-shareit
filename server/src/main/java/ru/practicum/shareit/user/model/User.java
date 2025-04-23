package ru.practicum.shareit.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "email")
@Entity
@Table(name = "user_list")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    public void updateFromAnotherUser(User updateUser) {
        if (updateUser.hasEmail()) email = updateUser.getEmail();
        if (updateUser.hasName()) name = updateUser.getName();
    }

    public boolean hasEmail() {
        return email != null;
    }

    public boolean hasName() {
        return name != null;
    }
}

