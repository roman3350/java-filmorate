package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.sql.*;
import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utilites.Validation.*;

@Component
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        // выполняем запрос к базе данных.
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT " +
                "f.id, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.mpa_NAME, f.GENRE_ID, g.genre_NAME " +
                "FROM FILMS AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN GENRE AS g ON f.GENRE_ID = g.GENRE_ID " +
                "WHERE f.ID = ?;", id);

        // обрабатываем результат выполнения запроса
        if (userRows.next()) {
            Film film = new Film(
                    userRows.getLong("id"),
                    userRows.getString("name"),
                    userRows.getString("description"),
                    userRows.getDate("RELEASE_DATE").toLocalDate(),
                    userRows.getInt("duration"),
                    new MPA(userRows.getLong("mpa_id"), userRows.getString("mpa_name")),
                    new Genre(userRows.getLong("genre_id"), userRows.getString("genre_name")));

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return Optional.of(film);
        } else {
            log.info("фильм с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Film create(Film film) {
        log.info("Запрос на создания фильма");
        validationFilm(film);
        String sqlQuery = "insert into films(name, description, RELEASE_DATE, duration, mpa_id, genre_id) " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            if (film.getMpa() == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setLong(5, film.getMpa().getId());
            }
            if (film.getGenre() == null) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setLong(6, film.getGenre().getId());
            }
            return stmt;
        }, keyHolder);
        log.info("Фильм добавлен");
        return findFilmById(keyHolder.getKey().longValue()).get();
    }

    @Override
    public Film update(Film film) {
        log.info("Запрос на обновление пользователя");
        if (findFilmById(film.getId()).isEmpty()) {
            log.warn("Пользователя с ID {} не найден", film.getId());
            throw new UserNotFoundException("Пользователя с таким ID нет");
        }
        validationFilm(film);
        String sqlQuery = "update films set " +
                "name = ?, description = ?, RELEASE_DATE = ?, duration = ?, mpa_id = ?, genre_id = ? " +
                "where id = ?";
        if (film.getMpa() == null && film.getGenre() == null) {
            jdbcTemplate.update(sqlQuery
                    , film.getName()
                    , film.getDescription()
                    , Date.valueOf(film.getReleaseDate())
                    , film.getDuration()
                    , null
                    , null
                    , film.getId());
        } else if (film.getMpa() == null) {
            jdbcTemplate.update(sqlQuery
                    , film.getName()
                    , film.getDescription()
                    , Date.valueOf(film.getReleaseDate())
                    , film.getDuration()
                    , null
                    , film.getGenre().getId()
                    , film.getId());
        } else if (film.getGenre() == null) {
            jdbcTemplate.update(sqlQuery
                    , film.getName()
                    , film.getDescription()
                    , Date.valueOf(film.getReleaseDate())
                    , film.getDuration()
                    , film.getMpa().getId()
                    , null
                    , film.getId());
        } else {
            jdbcTemplate.update(sqlQuery
                    , film.getName()
                    , film.getDescription()
                    , Date.valueOf(film.getReleaseDate())
                    , film.getDuration()
                    , film.getMpa().getId()
                    , film.getGenre().getId()
                    , film.getId());
        }
        log.info("Пользователь обновлен");
        return findFilmById(film.getId()).get();
    }

    public Collection<Film> findAll() {
        log.info("Запрос на вывод всех фильмов");
        String sqlQuery = "SELECT " +
                "f.id, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, " +
                "f.DURATION, f.MPA_ID, m.mpa_NAME, f.GENRE_ID, g.genre_NAME " +
                "FROM FILMS AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN GENRE AS g ON f.GENRE_ID = g.GENRE_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        log.info("Запрос лайк фильма");
        checkUserExists(userStorage.findUserById(userId));
        String sqlQuery = "insert into film_likes(film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
        log.info("Лайк поставлен");
        return findFilmById(filmId).get();
    }

    @Override
    public Film deleteLike(Long filmId, Long userId){
        log.info("Запрос на удаление лайка");
        String sqlQuery = String.format("SELECT EXISTS (SELECT * FROM FILM_LIKES " +
                "WHERE USER_ID = %d AND FILM_ID = %d);", userId, filmId);
        checkFriendExists(jdbcTemplate.queryForObject(sqlQuery, Boolean.class));
        checkUserExists(userStorage.findUserById(userId));
        sqlQuery = "delete from FILM_LIKES where user_id = ? and FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
        log.info("Пользователь удален из друзей");
        return findFilmById(filmId).get();
    }

    public Collection<Film> getFilmQuantityLike(int count){
        log.info("Запрос на вывод популярных фильмов");
        String sqlQuery = String.format("SELECT f.id, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, " +
                "f.DURATION, f.MPA_ID, m.mpa_NAME, f.GENRE_ID, g.genre_NAME " +
                "FROM FILMS AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN GENRE AS g ON f.GENRE_ID = g.GENRE_ID " +
                "WHERE f.ID IN (SELECT FILM_ID " +
                "FROM FILM_LIKES " +
                "GROUP BY FILM_ID " +
                "ORDER BY COUNT(FILM_ID)) " +
                "LIMIT(%d)",count);
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee);
    }

    private Film mapRowToEmployee(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(new MPA(rs.getLong("MPA_ID"), rs.getString("mpa_NAME")))
                .genre(new Genre(rs.getLong("GENRE_ID"),rs.getString("genre_NAME")))
                .build();
    }
}
