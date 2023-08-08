package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * добавление пользователя в друзья
     *
     * @param id       id пользователя
     * @param friendId id друга
     * @return добавленный друг
     */
    public User addFriend(Long id, Long friendId) {
        findUser(friendId);
        userStorage.findById(id).getFriends().add(friendId);
        userStorage.findById(friendId).getFriends().add(id);
        return userStorage.findById(friendId);
    }

    /**
     * удаление пользователя из друзей
     *
     * @param id       id пользователя
     * @param friendId id друга
     * @return друг удаленный из друзей
     */
    public User deleteFriend(Long id, Long friendId) {
        findUser(friendId);
        userStorage.findById(id).getFriends().remove(friendId);
        userStorage.findById(friendId).getFriends().remove(id);
        return userStorage.findById(friendId);
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
                .findById(id)
                .getFriends()
                        .stream()
                                .filter(userStorage
                                        .findById(friendId)
                                                .getFriends()::contains)
                                        .map(userStorage::findById)
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
                .findById(id)
                .getFriends()
                .stream()
                        .map(userStorage::findById)
                                .collect(Collectors.toList());
    }

    public void findUser(Long id) {
        if (userStorage.findById(id) == null) {
            throw new UserNotFoundException("Друга с таким Id нет");
        }
    }
}
