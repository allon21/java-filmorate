package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film film;
    private int currentId;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        currentId = 1;
    }

    private Film createFilm(String name, String description, LocalDate releaseDate, int duration) {
        return Film.builder()
                .id(currentId)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();
    }

    @Test
    void shouldThrowExceptionWhenFilmNameIsEmpty() {
        film = createFilm("", "film", LocalDate.of(2012, 12, 12), 101);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
        currentId++;
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        String longDescription = "a".repeat(201);
        film = createFilm("film", longDescription, LocalDate.of(2012, 12, 12), 101);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
        currentId++;
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        film = createFilm("film", "film", LocalDate.of(1775, 12, 12), 101);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
        currentId++;
        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNonPositive() {
        film = createFilm("film", "film", LocalDate.of(2012, 12, 12), 0);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
        currentId++;
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

}