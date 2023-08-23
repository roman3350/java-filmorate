package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utilites.Validation.*;

@Component
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        // выполняем запрос к базе данных.
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);

        // обрабатываем результат выполнения запроса
        if (userRows.next()) {
            User user = new User(
                    userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());

            log.info("Найден пользователь: {} {}", user.getId(), user.getEmail());
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        log.info("Запрос на создания пользователя");
        validationUser(user);
        String sqlQuery = "insert into users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        log.info("Пользователь добавлен");
        return findUserById(keyHolder.getKey().longValue()).get();
    }

    @Override
    public User update(User user) {
        log.info("Запрос на обновление пользователя");
        if (findUserById(user.getId()).isEmpty()) {
            log.warn("Пользователя с ID {} не найден", user.getId());
            throw new UserNotFoundException("Пользователя с таким ID нет");
        }
        validationUser(user);
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , Date.valueOf(user.getBirthday())
                , user.getId());
        log.info("Пользователь обновлен");
        return findUserById(user.getId()).get();
    }

    @Override
    public Collection<User> findAll() {
        log.info("Запрос на вывод всех пользователей");
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee);
    }

    @Override
    public User requestToFriend(Long id, Long friendId) {
        log.info("Запрос на заявку в друзья");
        checkUserExists(findUserById(friendId));
        String sqlQuery = "insert into friends(user_id, friend_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                id,
                friendId);
        log.info("Запрос отпарвлен");
        return findUserById(friendId).get();
    }

    @Override
    public User confirmFriend(Long confirmUserId, Long sendUserId) {
        log.info("Запрос на подтверждение дружбы");
        String sqlQuery = String.format("SELECT EXISTS (SELECT * FROM FRIENDS " +
                "WHERE USER_ID = %d AND FRIEND_ID = %d AND STATUS = FALSE);", sendUserId, confirmUserId);
        checkFriendExists(jdbcTemplate.queryForObject(sqlQuery, Boolean.class));
        sqlQuery = "update friends set " +
                "status = true " +
                "where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery
                , sendUserId
                , confirmUserId);
        log.info("Пользователь добавлен в друзья");
        return findUserById(sendUserId).get();
    }

    @Override
    public User deleteFriend(Long id, Long friendId) {
        log.info("Запрос на удаление из друзей");
        String sqlQuery = String.format("SELECT EXISTS (SELECT * FROM FRIENDS " +
                "WHERE USER_ID = %d AND FRIEND_ID = %d);", id, friendId);
        checkFriendExists(jdbcTemplate.queryForObject(sqlQuery, Boolean.class));
        sqlQuery = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.info("Пользователь удален из друзей");
        return findUserById(friendId).get();
    }

    @Override
    public Collection<User> getFriends(Long id) {
        log.info("Запрос на вывод друзей");
        String sqlQuery = "SELECT * " +
                "FROM USERS " +
                "WHERE ID IN (" +
                "SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE USER_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee, id);
    }

    @Override
    public Collection<User> commonFriends(Long id, Long friendId) {
        log.info("Запрос на вывод общих друзей");
        String sqlQuery = "SELECT * " +
                "FROM USERS " +
                "WHERE ID IN (" +
                "SELECT friend_id " +
                "FROM FRIENDS " +
                "WHERE USER_id IN (?,?) " +
                "GROUP BY FRIEND_ID " +
                "HAVING COUNT(FRIEND_ID)>1)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee, id, friendId);
    }

    private User mapRowToEmployee(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }


}
