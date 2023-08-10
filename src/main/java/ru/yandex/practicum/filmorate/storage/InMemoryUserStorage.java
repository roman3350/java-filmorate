package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utilites.Validation.validationUser;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id;

    /**
     * Проверяет данные, при успешной проверке добавляет объект пользователя в мапу
     *
     * @param user объект пользователя с данными
     * @return объект добавленного пользователя
     */
    public User create(User user) {
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
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователя с ID {} не найден", user.getId());
            throw new UserNotFoundException(String.format("Пользователя с {ID} нет", id));
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
    public Collection<User> findAll() {
        return users.values();
    }

    /**
     * поиск пользователя по id
     *
     * @param id id пользователя
     * @return пользователь
     */
    public User findUserById(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователя с ID {} не найден", id);
            throw new UserNotFoundException(String.format("Пользователя с {ID} нет", id));
        }
        return users.get(id);
    }
}
