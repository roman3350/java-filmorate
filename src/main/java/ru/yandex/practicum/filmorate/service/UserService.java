package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    public User findUserById(@PathVariable Long id) {
        return userStorage.findUserById(id);
    }

    /**
     * добавление пользователя в друзья
     *
     * @param id       id пользователя
     * @param friendId id друга
     * @return добавленный друг
     */
    public User addFriend(Long id, Long friendId) {
        checkUserExists(friendId, userStorage);
        userStorage.findUserById(id).getFriends().add(friendId);
        userStorage.findUserById(friendId).getFriends().add(id);
        return userStorage.findUserById(friendId);
    }

    /**
     * удаление пользователя из друзей
     *
     * @param id       id пользователя
     * @param friendId id друга
     * @return друг удаленный из друзей
     */
    public User deleteFriend(Long id, Long friendId) {
        checkUserExists(friendId, userStorage);
        userStorage.findUserById(id).getFriends().remove(friendId);
        userStorage.findUserById(friendId).getFriends().remove(id);
        return userStorage.findUserById(friendId);
    }

    /**
     * вывод общих друзей с пользователем
     *
     * @param id       id пользователя
     * @param friendId id пользователя с которым надо вывести общих друзей
     * @return общие друзья
     */
    public List<User> commonFriends(Long id, Long friendId) {
        return userStorage
                .findUserById(id)
                .getFriends()
                .stream()
                .filter(userStorage
                        .findUserById(friendId)
                        .getFriends()::contains)
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }

    /**
     * вывести друзей пользователя
     *
     * @param id id пользователя
     * @return друзья
     */
    public List<User> getFriends(Long id) {
        return userStorage
                .findUserById(id)
                .getFriends()
                .stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }
}
