package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public final List<User> getAllUsers() {
        log.info("Получение списка всех пользователей.");
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с ID {} не найден для обновления.", user.getId());
            throw new NotFoundException("Пользователь с таким ID не найден");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлён: {}", user);
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (users.containsValue(user)) {
            log.error("Попытка создать пользователя, который уже существует: {}", user.getLogin());
            throw new ValidationException("Такой пользователь уже существует.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации: электронная почта не может быть пустой и должна содержать символ @.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }

        if (user.getLogin().isBlank() || user.getLogin().matches(".*\\s+.*")) {
            log.error("Ошибка валидации: логин не может быть пустым и содержать пробелы.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
