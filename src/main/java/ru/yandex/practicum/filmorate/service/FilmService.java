package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * Принимает из пути id и выводит фильм по этому id
     *
     * @param id id фильма
     * @return фильм по id
     */

    public Optional<Film> findFilmById(Long id) {
        return filmStorage.findFilmById(id);
    }

    /**
     * Принимает из тела запроса фильм и добавляет его
     *
     * @param film объект фильма
     * @return добавленный объект фильма
     */

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    /**
     * Принимает из тела запроса фильм и обновляет его
     *
     * @param film объект фильма
     * @return обновленный фильм
     */

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    /**
     * Вывод всех добавленные фильмы
     *
     * @return список всех фильмов
     */

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    /**
     * Добавляет лайк фильму
     *
     * @param filmId id фильма
     * @param userId id пользователя ставящего лайк
     * @return лайкнутый фильм
     */

    public Film addLike(Long filmId, Long userId) {
        return filmStorage.addLike(filmId, userId);
    }

    /**
     * удаляет лайк с фильма
     *
     * @param filmId id фильма
     * @param userId id пользователя чей лайк надо удалить
     * @return фильм с которого удалили лайк
     */

    public Film deleteLike(Long filmId, Long userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

    /**
     * Вывод фильмов по популярности
     *
     * @param count количество фильмов которые надо вывести
     * @return список фильмов
     */

    public Collection<Film> getFilmQuantityLike(int count) {
        return filmStorage.getFilmQuantityLike(count);
    }

}
