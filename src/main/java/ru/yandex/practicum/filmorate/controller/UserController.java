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
    private long id = 0;
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public final List<User> getAllUsers() {
        log.info("Получение списка всех пользователей.");
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        long userId = user.getId();
        if (!users.containsKey(userId)) {
            log.error("Пользователь с ID {} не найден для обновления.", userId);
            throw new NotFoundException("Пользователь не найден.");
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
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь {} создан с ID {}", user.getLogin(), user.getId());
        return user;
    }

}
