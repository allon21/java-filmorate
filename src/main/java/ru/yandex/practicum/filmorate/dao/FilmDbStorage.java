package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String checkMpaSql = "SELECT COUNT(*) FROM mpa_rating WHERE rating_id = ?";
        Integer mpaExists = jdbcTemplate.queryForObject(checkMpaSql, Integer.class, film.getMpa().getId());

        if (mpaExists == null || mpaExists == 0) {
            throw new BadRequestException("MPA рейтинг с ID " + film.getMpa().getId() + " не существует.");
        }

        String sql = "INSERT INTO film (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId().intValue());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);
        log.info("film id : " + film.getId());
        log.info("film : " + film);

        filmExist(film.getId());
        saveFilmGenres(film);
        return film;
    }


    private void saveFilmGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, film.getGenres().stream()
                    .distinct()
                    .map(genre -> new Object[]{film.getId(), genre.getId()})
                    .toList());
        }
    }


    @Override
    public Film updateFilm(Film film) {
        filmExist(film.getId());

        jdbcTemplate.update("UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, "
                        + "rating_id = ? WHERE film_id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?",
                film.getId());

        saveFilmGenres(film);

        return film;
    }


    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM film JOIN mpa_rating ON film.rating_id = mpa_rating.rating_id";
        List<Film> filmList = jdbcTemplate.query(sql, new FilmMapper());

        Map<Long, List<Genre>> genresMap = getGenresForFilms();
        filmList.forEach(film -> film.setGenres(genresMap.getOrDefault(film.getId(), List.of())));

        return filmList;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return Map.of();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private Map<Long, List<Genre>> getGenresForFilms() {
        String sqlGenre = "SELECT film_id, genre.genre_id, genre.name FROM film_genre JOIN genre ON genre.genre_id = film_genre.genre_id";

        return jdbcTemplate.query(sqlGenre, (rs, rowNum) -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("name"));
            return new AbstractMap.SimpleEntry<>(filmId, genre);
        }).stream().collect(Collectors.groupingBy(Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    @Override
    public Film getFilmById(Long id) {
        filmExist(id);
        String sqlFilm = "SELECT * FROM film JOIN mpa_rating ON film.rating_id = mpa_rating.rating_id WHERE film_id = ?";
        String sqlGenre = "SELECT * FROM film_genre JOIN genre ON genre.genre_id = film_genre.genre_id WHERE film_id = ?";

        Film film = jdbcTemplate.queryForObject(sqlFilm, new FilmMapper(), id);

        List<Genre> genres = jdbcTemplate.query(sqlGenre, new GenreMapper(), id);
        film.setGenres(genres);

        return film;
    }

    @Override
    public void filmExist(Long id) {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film WHERE film_id = ?", Integer.class, id);

            if (count == null || count == 0) {
                throw new NotFoundException("Фильм с ID " + id + " не найден.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка доступа к данным: " + e.getMessage(), e);
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, " +
                "mpa.rating_id AS mpa_rating_id, mpa.name AS mpa_rating_name, " +
                "COUNT(fl.user_id) AS like_count " +
                "FROM film f " +
                "LEFT JOIN film_like fl ON f.film_id = fl.film_id " +
                "JOIN mpa_rating mpa ON f.rating_id = mpa.rating_id " +
                "GROUP BY f.film_id " +
                "HAVING like_count > 0 " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmMapper(), count);
    }
}