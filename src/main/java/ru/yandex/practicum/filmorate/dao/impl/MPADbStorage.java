package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenryStorage;
import ru.yandex.practicum.filmorate.dao.MPAStorage;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utilites.Validation.checkGenreExists;

@Component
public class MPADbStorage implements MPAStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<MPA> findMPAById(Long id) {
        // выполняем запрос к базе данных.
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from mpa where mpa_id = ?", id);

        // обрабатываем результат выполнения запроса
        if (userRows.next()) {
            MPA mpa = new MPA(
                    userRows.getLong("mpa_id"),
                    userRows.getString("mpa_name"));

            log.info("Найден рейтинг: {} {}", mpa.getId(), mpa.getName());
            return Optional.of(mpa);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Collection<MPA> findAll() {
        log.info("Запрос на вывод всех жанров");
        String sqlQuery = "select * from mpa  " +
                "ORDER BY mpa_ID ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee);
    }

    private MPA mapRowToEmployee(ResultSet rs, int rowNum) throws SQLException {
        return MPA.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
