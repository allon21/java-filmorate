package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreDbStorage;

    public List<Genre> getAllGenres() {

        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        Genre genre = genreDbStorage.getGenreById(id);
        return genre;
    }

    public void genreExist(Long id) {
        genreDbStorage.genreExist(id);
    }

}
