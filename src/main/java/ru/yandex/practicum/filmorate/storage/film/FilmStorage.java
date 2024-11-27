package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    List<Film> getAllFilms();

    Map<Long, Film> getFilms();

    boolean isEmpty();

    List<Film> getTopFilms(int count);

    void filmExist(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}
