package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.custom.DuplicateException;
import ru.practicum.shareit.exception.custom.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.dto.MapperUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MapperUserDto mapperUserDto;

    @Override
    public GetUserDto getUserById(Long id) {
        log.trace("Попытка получить пользователя с id = {}", id);
        User ans = getUserByIdOrThrowNotFound(id);
        log.trace("Пользователь с id = {}, успешно получен", id);
        return mapperUserDto.userToGetDto(ans);
    }

    @Override
    @Transactional
    public GetUserDto createUser(CreateUserDto createUserDto) {
        log.trace("Попытка создать пользователя email = {}, name = {}",
                createUserDto.getEmail(),
                createUserDto.getName());
        throwDuplicateIfEmailInStorage(createUserDto.getEmail());
        User user = mapperUserDto.createDtoToUser(createUserDto);
        userRepository.save(user);
        log.trace("Пользователь успешно создан, id = {}", user.getId());
        return mapperUserDto.userToGetDto(user);
    }

    @Override
    @Transactional
    public GetUserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        log.trace("Попытка обновить пользователя с id = {}", id);
        User currentUser = getUserByIdOrThrowNotFound(id);
        User updatedUser = mapperUserDto.updateDtoToUser(updateUserDto);

        log.trace("Проверка email в updateUser()");
        if (updatedUser.hasEmail() && !updatedUser.getEmail().equals(currentUser.getEmail())) {
            throwDuplicateIfEmailInStorage(updatedUser.getEmail());
            log.debug("Email не совпадает. Новый email = {}, Старый email = {}",
                    updatedUser.getEmail(),
                    currentUser.getEmail());
        }

        currentUser.updateFromAnotherUser(updatedUser);
        userRepository.save(currentUser);

        log.trace("Пользователь с id = {}, успешно обновлен", id);
        return mapperUserDto.userToGetDto(currentUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        log.trace("Попытка удалить пользователя с id = {}", id);
        userRepository.deleteById(id);
        itemRepository.deleteByOwnerId(id);
        log.trace("Пользователь с id = {}, успешно удалён, если существовал", id);
    }

    private User getUserByIdOrThrowNotFound(Long id) {
        log.trace("Проверка пользователя с id = {} в хранилище", id);
        Optional<User> optionalUser = userRepository.findById(id);
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
        if (email != null && userRepository.findByEmailEqualsIgnoreCase(email).isPresent()) {
            throw new DuplicateException("Email " + email + " уже используется");
        }
        log.trace("Email = {}, свободен", email);
    }
}
