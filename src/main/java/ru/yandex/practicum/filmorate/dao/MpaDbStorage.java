package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa_rating ORDER BY rating_id";
        return jdbcTemplate.query(sql, new MpaMapper());
    }

    @Override
    public Mpa getMpaById(Long id) {
        mpaExist(id);
        String sql = "SELECT * FROM mpa_rating WHERE rating_id = ?";
        return jdbcTemplate.queryForObject(sql, new MpaMapper(), id);
    }

    @Override
    public void mpaExist(Long id) {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mpa_rating WHERE rating_id = ?", Integer.class, id);
            if (count == 0 || count == null) {
                throw new NotFoundException("Рейтинг с ID " + id + " не найден.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка доступа к данным: " + e.getMessage(), e);
        }
    }
}
