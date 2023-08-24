package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenryStorage;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utilites.Validation.checkGenreExists;

@Component
public class GenreDbStorage implements GenryStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> findGenreById(Long id) {
        // выполняем запрос к базе данных.
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);

        // обрабатываем результат выполнения запроса
        if (userRows.next()) {
            Genre genre = new Genre(
                    userRows.getLong("genre_id"),
                    userRows.getString("genre_name"));

            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
            return Optional.of(genre);
        } else {
            log.info("жанр с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Collection<Genre> findAll() {
        log.info("Запрос на вывод всех жанров");
        String sqlQuery = "select * from genre  " +
                "ORDER BY GENRE_ID ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee);
    }

    private Genre mapRowToEmployee(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
