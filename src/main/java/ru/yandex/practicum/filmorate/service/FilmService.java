package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
     * Добавляет лайк фильму
     *
     * @param filmId id фильма
     * @param userId id пользователя ставящего лайк
     * @return лайкнутый фильм
     */
    public Film addLike(Long filmId, Long userId) {
        findUser(userId);
        filmStorage.findById(filmId).getIdLike().add(userId);
        return filmStorage.findById(filmId);
    }

    /**
     * удаляет лайк с фильма
     *
     * @param filmId id фильма
     * @param userId id пользователя чей лайк надо удалить
     * @return фильм с которого удалили лайк
     */
    public Film deleteLike(Long filmId, Long userId) {
        findUser(userId);
        filmStorage.findById(filmId).getIdLike().remove(userId);
        return filmStorage.findById(filmId);
    }

    /**
     * Вывод фильмов по популярности
     *
     * @param count количество фильмов которые надо вывести
     * @return список фильмов
     */
    public List<Film> getFilmQuantityLike(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(p0 -> -p0.getIdLike().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public void findUser(Long id) {
        if (userStorage.findById(id) == null) {
            throw new UserNotFoundException("Друга с таким Id нет");
        }
    }
}
