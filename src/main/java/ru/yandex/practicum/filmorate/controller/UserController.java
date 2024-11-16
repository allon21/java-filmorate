package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController {
    private long Id = 0;
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public final List<User> getAllUsers() {
        log.info("Получение списка всех пользователей.");
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        validateUser(user);
        long userId = user.getId();
        if (!users.containsKey(userId)) {
            log.error("Пользователь с ID {} не найден для обновления.", userId);
            throw new ValidationException("Пользователь не найден.");
        }
        users.put(userId, user);
        log.info("Пользователь {} с ID {} обновлен.", user.getLogin(), userId);
        return user;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (users.containsValue(user)) {
            log.error("Попытка создать пользователя, который уже существует: {}", user.getLogin());
            throw new ValidationException("Такой пользователь уже существует.");
        }
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++Id);
        users.put(user.getId(), user);
        log.info("Пользователь {} создан с ID {}", user.getLogin(), user.getId());
        return user;
    }



    public final void validateUser(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации: электронная почта не может быть пустой и должна содержать символ @.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }

        if (user.getLogin().isBlank() || user.getLogin().matches(".*\\s+.*")) {
            log.error("Ошибка валидации: логин не может быть пустым и содержать пробелы.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
