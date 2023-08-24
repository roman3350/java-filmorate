package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создает пользователя
     *
     * @param user объект пользователя
     * @return созданный пользователь
     */
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    /**
     * Обновляет пользователя
     *
     * @param user объект пользователя
     * @return обновленный пользователь
     */
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    /**
     * Список всех пользователей
     *
     * @return лист всех пользователей
     */
    @GetMapping()
    public Collection<User> findAll() {
        return userService.findAll();
    }

    /**
     * Возвращает пользователя по id
     *
     * @param id id пользователя
     * @return пользователь
     */
    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    /**
     * Отправляет запрос в друзья
     *
     * @param id       id пользователя
     * @param friendId id друга
     * @return пользователь, которому отправили запрос
     */
    @PutMapping("/{id}/friends/{friendId}")
    public User requestToFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.requestToFriend(id, friendId);
    }

    @PutMapping("/{id}/confirmFriend/{friendId}")
    public User confirmFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.confirmFriend(id, friendId);
    }

    /**
     * Удаляет пользователя из друзей
     *
     * @param id       id пользователя
     * @param friendId id друга
     * @return удаленный друг
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    /**
     * Вывод всех друзей пользователя
     *
     * @param id id пользователя
     * @return друзя пользователя
     */
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    /**
     * вывод общих друзей с пользователя
     *
     * @param id      Id пользователя
     * @param otherId id пользователя с которым выводятся общие друзья
     * @return общие друзья
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.commonFriends(id, otherId);
    }
}
