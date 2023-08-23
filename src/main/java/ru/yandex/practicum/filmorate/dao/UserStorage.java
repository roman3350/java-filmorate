package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    Optional<User> findUserById(Long id);

    User requestToFriend(Long id, Long friendId);

    User confirmFriend(Long confirmUserId, Long sendUserId);

    User deleteFriend(Long id, Long friendId);

    Collection<User> getFriends(Long id);

    Collection<User> commonFriends(Long id, Long friendId);
}