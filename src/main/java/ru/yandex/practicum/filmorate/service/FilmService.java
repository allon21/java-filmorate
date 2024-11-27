package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreService genreService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreService = genreService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                try {
                    genreService.genreExist(genre.getId());
                } catch (Exception e) {
                    throw new BadRequestException(e.getMessage());
                }
            });
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        validateFilmId(id);
        Film film = filmStorage.getFilmById(id);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        validateUserId(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        validateUserId(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilmsByLikes(int count) {
        if (filmStorage.isEmpty()) {
            throw new NotFoundException("Список фильмов пуст.");
        }
        return filmStorage.getTopFilms(count);
    }

    public Film findFilmById(Long id) {
        validateFilmId(id);
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильм с запрашиваемым " + id + " отсутствует.)");
        }
        return filmStorage.getFilms().get(id);
    }

    private void validateFilm(Film film) {
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
    }

    private void validateFilmId(Long id) {
        if (id <= 0 || id == null) {
            throw new EmptyIdException();
        }
    }

    private void validateUserId(Long id) {
        if (id <= 0) {
            throw new EmptyIdException();
        }
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("User с id " + id + " не найден.");
        }
    }
}