package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    /**
     * Проверяет данные, при успешной проверке добавляет объект пользователя в мапу
     *
     * @param user объект пользователя с данными
     * @return объект добавленного пользователя
     */
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Запрос на создания пользователя");
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("электронная почта пустая или не содержит символ @");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("логин пустой или содержит пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем ");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Фильм добавлен");
        return user;
    }

    /**
     * Проверяет данные, при успешной проверке обновляет пользователя
     *
     * @param user объект пользователя с данными
     * @return обновленный объект пользователя
     */
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователя с ID {} не найден", user.getId());
            throw new ValidationException("Пользователя с таким ID нет");
        }
        log.info("Запрос на обновление пользователя");
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("электронная почта пустая или не содержит символ @");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("логин пустой или содержит пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем ");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        users.put(user.getId(), user);
        log.info("Фильм обновлен");
        return user;
    }

    /**
     * Возвращает всех добавленных пользователей
     *
     * @return добавленные пользователи
     */
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }
}
