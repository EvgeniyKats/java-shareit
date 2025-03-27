package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "email")
@Builder
public class User {
    private Long id;
    private String email;
    private String name;

    public User updateFromAnotherUser(User updateUser) {
        if (updateUser.hasEmail()) email = updateUser.getEmail();
        if (updateUser.hasName()) name = updateUser.getName();
        return this;
    }

    boolean hasEmail() {
        return email != null;
    }

    boolean hasName() {
        return name != null;
    }
}
