package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.custom.DuplicateException;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public User getUserById(Long id) {
        log.trace("Попытка получить пользователя с id = {}", id);
        return getUserByIdOrThrowNotFound(id);
    }

    @Override
    public User createUser(User user) {
        log.trace("Попытка создать пользователя email = {}, name = {}", user.getEmail(), user.getName());
        throwDuplicateIfEmailInStorage(user.getEmail());
        userRepository.createUser(user);
        log.trace("Пользователь успешно создан, id = {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        log.trace("Попытка обновить пользователя с id = {}", id);
        User currentUser = getUserByIdOrThrowNotFound(id);
        boolean needToChangeEmail;

        log.trace("Проверка email в updateUser()");
        if (updatedUser.hasEmail() && !updatedUser.getEmail().equals(currentUser.getEmail())) {
            throwDuplicateIfEmailInStorage(updatedUser.getEmail());
            log.debug("Email не совпадает. Новый email = {}, Старый email = {}",
                    updatedUser.getEmail(),
                    currentUser.getEmail());
            needToChangeEmail = true;
        } else {
            log.trace("Email совпадает или не должен быть обновлен");
            needToChangeEmail = false;
        }

        return userRepository.updateUser(id, updatedUser, needToChangeEmail);
    }

    @Override
    public void deleteUserById(Long id) {
        log.trace("Попытка удалить пользователя с id = {}", id);
        if (!userRepository.deleteUserById(id)) {
            throw NotFoundException.builder()
                    .setNameObject("Пользователь")
                    .setNameParameter("id")
                    .setValueParameter(id)
                    .build();
        }
        log.trace("Пользователь с id = {}, успешно удалён", id);
        itemRepository.deleteAllItemsByUserId(id);
    }

    private User getUserByIdOrThrowNotFound(Long id) {
        log.trace("Проверка пользователя с id = {} в хранилище", id);
        Optional<User> optionalUser = userRepository.getUserById(id);
        if (optionalUser.isEmpty()) {
            throw NotFoundException.builder()
                    .setNameObject("Пользователь")
                    .setNameParameter("id")
                    .setValueParameter(id)
                    .build();
        }
        log.trace("Пользователь с id = {} найден", id);
        return optionalUser.get();
    }

    private void throwDuplicateIfEmailInStorage(String email) {
        log.trace("Проверка пользователя с email = {} в хранилище", email);
        if (email != null && userRepository.getUserByEmail(email).isPresent()) {
            throw new DuplicateException("Email " + email + " уже используется");
        }
        log.trace("Email = {}, свободен", email);
    }
}
