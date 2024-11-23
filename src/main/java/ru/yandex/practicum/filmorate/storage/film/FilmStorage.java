package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void remove(int id);

    Film getFilmById(Integer id);

    List<Film> getAllFilms();

    boolean isEmpty();

    List<Film> getTopFilms(int count);
}