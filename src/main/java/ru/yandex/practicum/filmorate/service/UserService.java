package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utilites.Validation.checkUserExists;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Создает пользователя
     *
     * @param user объект пользователя
     * @return созданный пользователь
     */
    public User create(User user) {
        return userStorage.create(user);
    }

    /**
     * Обновляет пользователя
     *
     * @param user объект пользователя
     * @return обновленный пользователь
     */
    public User update(User user) {
        return userStorage.update(user);
    }

    /**
     * Список всех пользователей
     *
     * @return лист всех пользователей
     */
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    /**
     * Возвращает пользователя по id
     *
     * @param id id пользователя
     * @return пользователь
     */
    public User findUserById(Long id) {
        Optional<User> user = userStorage.findUserById(id);
        checkUserExists(user);
        return userStorage.findUserById(id).get();
    }

    /**
     * добавление пользователя в друзья
     *
     * @param id       id пользователя
     * @param friendId id друга
     * @return добавленный друг
     */
    public User requestToFriend(Long id, Long friendId) {
        return userStorage.requestToFriend(id, friendId);
    }

    public User confirmFriend(Long id, Long friendId) {
        return userStorage.confirmFriend(id, friendId);
    }

    /**
     * удаление пользователя из друзей
     *
     * @param id       id пользователя
     * @param friendId id друга
     * @return друг удаленный из друзей
     */
    public User deleteFriend(Long id, Long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    /**
     * вывод общих друзей с пользователем
     *
     * @param id       id пользователя
     * @param friendId id пользователя с которым надо вывести общих друзей
     * @return общие друзья
     */
    public Collection<User> commonFriends(Long id, Long friendId) {
        return userStorage.commonFriends(id, friendId);
    }

    /**
     * вывести друзей пользователя
     *
     * @param id id пользователя
     * @return друзья
     */
    public Collection<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }
}
