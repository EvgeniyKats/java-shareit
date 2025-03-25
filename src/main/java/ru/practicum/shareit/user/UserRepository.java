package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    User createUser(User user);

    User updateUser(Long id, User updatedUser, boolean needToChangeEmail);

    boolean deleteUserById(Long id);
}
