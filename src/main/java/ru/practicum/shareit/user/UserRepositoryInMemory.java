package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> userById = new HashMap<>();
    private final Map<String, User> userByEmail = new HashMap<>();
    private Long idUserNext = 1L;

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userById.get(id));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userByEmail.get(email));
    }

    @Override
    public User createUser(User user) {
        user.setId(idUserNext++);
        userById.put(user.getId(), user);
        userByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User updateUser(Long id, User updatedUser, boolean needToChangeEmail) {
        User currentUser = userById.get(id);

        if (needToChangeEmail) {
            userByEmail.remove(currentUser.getEmail());
            userByEmail.put(updatedUser.getEmail(), currentUser);
        }

        return currentUser.updateFromAnotherUser(updatedUser);
    }

    @Override
    public boolean deleteUserById(Long id) {
        User user = userById.remove(id);
        if (user == null) return false;
        userByEmail.remove(user.getEmail());
        return true;
    }
}
