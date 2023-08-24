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
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.*;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
                "f.id, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.mpa_NAME " +
                "FROM FILMS AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "WHERE f.ID = ?", id);

        // обрабатываем результат выполнения запроса
        if (userRows.next()) {
            Film film = Film.builder()
                    .id(userRows.getLong("id"))
                    .name(userRows.getString("name"))
                    .description(userRows.getString("description"))
                    .releaseDate(userRows.getDate("RELEASE_DATE").toLocalDate())
                    .duration(userRows.getInt("duration"))
                    .mpa(new MPA(userRows.getLong("MPA_ID"), userRows.getString("mpa_NAME")))
                    .genres(Set.of())
                    .build();
            String sqlQueryGenre = "SELECT " +
                    "g.genre_id, g.GENRE_NAME " +
                    "FROM FILM_GENRE AS fg " +
                    "LEFT JOIN GENRE AS g ON fg.GENRE_ID  = g.GENRE_ID " +
                    "WHERE fg.FILM_ID = ? " +
                    "ORDER BY g.genre_id DESC";
            film.setGenres(Set.copyOf(jdbcTemplate.query(sqlQueryGenre, this::mapRowToGenre, id))
                    .stream()
                    .sorted((g0,g1)-> (int) (g0.getId()-g1.getId()))
                    .collect(Collectors.toSet()));
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return Optional.of(film);
        } else {
            log.info("фильм с идентификатором {} не найден.", id);
            checkFilmExists(Optional.empty());
            return Optional.empty();
        }
    }

    @Override
    public Film create(Film film) {
        log.info("Запрос на создания фильма");
        validationFilm(film);
        String sqlQuery = "insert into films(name, description, RELEASE_DATE, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            if (film.getMpa() == null) {
                stmt.setNull(5, Types.NULL);
            } else {
                stmt.setLong(5, film.getMpa().getId());
            }
            return stmt;
        }, keyHolder);
        if (film.getGenres() != null) {
            if (!film.getGenres().isEmpty()) {
                String sqlQueryGenre = "insert into film_genre(film_id, genre_id) " +
                        "values (?, ?)";
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(sqlQueryGenre,
                            keyHolder.getKey().longValue(),
                            genre.getId());
                }
            }
        }
        log.info("Фильм добавлен");
        return findFilmById(keyHolder.getKey().longValue()).get();
    }

    @Override
    public Film update(Film film) {
        log.info("Запрос на обновление фильма");
        if (findFilmById(film.getId()).isEmpty()) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new FilmNotFoundException("Фильм с таким ID нет");
        }
        validationFilm(film);
        String sqlQuery = "update films set " +
                "name = ?, description = ?, RELEASE_DATE = ?, duration = ?, mpa_id = ?" +
                "where id = ?";
        if (film.getMpa() == null) {
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    null,
                    film.getId());
        } else {
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
        }
        if (film.getGenres() != null) {
            if (!film.getGenres().isEmpty()) {
                String sqlQueryGenreDelete = "delete from film_genre where film_id = ? ";
                jdbcTemplate.update(sqlQueryGenreDelete,
                        film.getId());
                String sqlQueryGenre = "insert into film_genre(film_id, genre_id) " +
                        "values (?, ?) " +
                        "ON CONFLICT DO NOTHING";
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(sqlQueryGenre,
                            film.getId(),
                            genre.getId());
                }
            } else {
                String sqlQueryGenreDelete = "delete from film_genre where film_id = ? ";
                jdbcTemplate.update(sqlQueryGenreDelete,
                        film.getId());
            }
        }
        log.info("Пользователь обновлен");
        return findFilmById(film.getId()).get();
    }

    public Collection<Film> findAll() {
        log.info("Запрос на вывод всех фильмов");
        String sqlQuery = "SELECT " +
                "f.id, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, " +
                "f.DURATION, f.MPA_ID, m.mpa_NAME, ge.GENRE_ID, ge.genre_NAME " +
                "FROM FILMS AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILM_GENRE AS g ON f.id = g.FILM_ID " +
                "LEFT JOIN GENRE AS ge ON g.GENRE_ID = ge.GENRE_ID ";
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
    public Film deleteLike(Long filmId, Long userId) {
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

    public Collection<Film> getFilmQuantityLike(int count) {
        log.info("Запрос на вывод популярных фильмов");
        String sqlQuery = String.format("SELECT " +
                "f.id, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.mpa_NAME " +
                "FROM FILMS AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILM_LIKES AS fl ON f.id = fl.FILM_ID " +
                "GROUP BY f.ID " +
                "ORDER BY COUNT(fl.FILM_ID) DESC " +
                "LIMIT(%d)", count);
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private Film mapRowToEmployee(ResultSet rs, int rowNum) throws SQLException {
        String sqlQueryGenre = "SELECT " +
                "g.genre_id, g.GENRE_NAME " +
                "FROM FILM_GENRE AS fg " +
                "LEFT JOIN GENRE AS g ON fg.GENRE_ID  = g.GENRE_ID " +
                "WHERE fg.FILM_ID = ? " +
                "ORDER BY g.genre_id DESC";
        if (rs.getLong("mpa_id") == 0) {
            return Film.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration(rs.getInt("DURATION"))
                    .genres(Set.of())
                    .build();
        } else {
            return Film.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration(rs.getInt("DURATION"))
                    .mpa(new MPA(rs.getLong("MPA_ID"), rs.getString("mpa_NAME")))
                    .genres(Set.copyOf(jdbcTemplate.query(sqlQueryGenre,
                            this::mapRowToGenre,
                            rs.getLong("id")))
                            .stream()
                    .sorted((g0,g1)-> (int) (g0.getId()-g1.getId()))
                    .collect(Collectors.toSet()))
                    .build();
        }
    }
}
