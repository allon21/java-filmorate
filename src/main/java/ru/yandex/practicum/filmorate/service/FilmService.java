package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации: название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года;");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации: продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Ошибка валидации: максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года;");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new EmptyIdException();
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("User с id " + userId + "не найден.");
        }
        findFilmById(filmId).getIdUsersLikedFilm().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new EmptyIdException();
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("User с id " + userId + "не найден.");
        }
        if (findFilmById(filmId).getIdUsersLikedFilm().isEmpty()) {
            throw new NotFoundException("Список фильмов пуст.");
        }
        findFilmById(filmId).getIdUsersLikedFilm().remove(userId);
    }

    public List<Film> getTopFilmsByLikes(Integer count) {
        if (filmStorage.isEmpty()) {
            throw new NotFoundException("Список фильмов пуст.");
        }
        return filmStorage.getTopFilms(count);
    }

    public Film findFilmById(Integer id) {
        if (id <= 0) {
            throw new EmptyIdException();
        }
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильм с запрашиваемым " + id + " отсутствует.)");
        }
        return filmStorage.getFilms().get(id);
    }

}