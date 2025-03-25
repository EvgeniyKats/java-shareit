package ru.practicum.shareit.user;

public interface UserService {
    User getUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User updatedUser);

    void deleteUserById(Long id);
}
