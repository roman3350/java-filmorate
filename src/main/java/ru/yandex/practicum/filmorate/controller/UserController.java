package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utilites.Validation.validationUser;

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
        validationUser(user);
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
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
        validationUser(user);
        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
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
