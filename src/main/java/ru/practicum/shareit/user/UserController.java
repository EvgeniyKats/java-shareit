package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public GetUserDto getUserById(@Min(1) @PathVariable Long id) {
        log.info("Получен GET /users/{}", id);
        GetUserDto ans = userService.getUserById(id);
        log.info("Пользователь с id = {}, успешно найден", id);
        return ans;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GetUserDto createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        log.info("Получен POST /users, параметры email = {}, name = {}",
                createUserDto.getEmail(),
                createUserDto.getName());
        GetUserDto ans = userService.createUser(createUserDto);
        log.info("Пользователь успешно создан, id = {}", ans.getId());
        return ans;
    }

    @PatchMapping("/{id}")
    public GetUserDto updateUser(@Min(1) @PathVariable Long id,
                                 @Valid @RequestBody UpdateUserDto updateUserDto) {
        log.info("Получен PATCH /users/{}, параметры email = {}, name = {}",
                id,
                updateUserDto.getEmail(),
                updateUserDto.getName());
        GetUserDto ans = userService.updateUser(id, updateUserDto);

        log.info("Пользователь успешно обновлен, его параметры, id = {}, email = {}, name = {}",
                ans.getId(),
                ans.getEmail(),
                ans.getName());
        return ans;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@Min(1) @PathVariable Long id) {
        log.info("Получен DELETE /users/{}",
                id);
        userService.deleteUserById(id);
        log.info("Пользователь успешно удалён, id = {}", id);
    }
}
