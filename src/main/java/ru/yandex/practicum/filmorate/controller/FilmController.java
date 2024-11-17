package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    @NotNull
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение списка всех фильмов.");
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Попытка обновления несуществующего фильма с ID: {}", film.getId());
            throw new NotFoundException("Фильм с таким ID не найден");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года;");
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм с ID: {}", film.getId());
        return film;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (films.containsValue(film)) {
            log.error("Попытка добавить фильм, который уже существует: {}", film.getName());
            throw new ValidationException("Такой фильм уже добавлен.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года;");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
