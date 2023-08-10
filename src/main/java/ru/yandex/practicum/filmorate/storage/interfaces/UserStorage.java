package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {


    public User create(User user);


    public User update(User user);


    public Collection<User> findAll();

    User findUserById(Long id);
}
