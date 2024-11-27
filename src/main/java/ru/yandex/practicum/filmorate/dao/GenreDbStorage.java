package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public Genre getGenreById(Long id) {
        genreExist(id);
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, new GenreMapper(), id);
    }

    @Override
    public void genreExist(Long id) {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM genre WHERE genre_id = ?", Integer.class, id);
            if (count == 0 || count == null) {
                throw new NotFoundException("Жанр с ID " + id + " не найден.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка доступа к данным: " + e.getMessage(), e);
        }
    }
}