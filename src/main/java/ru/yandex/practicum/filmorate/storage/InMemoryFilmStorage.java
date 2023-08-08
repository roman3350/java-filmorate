package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utilites.Validation.validationFilm;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private long id;

    /**
     * Проверяет данные, при успешной проверке добавляет объект фильма в мапу
     *
     * @param film объект фильма
     * @return добавленный объект фильма
     */
    public Film create(Film film) {
        log.info("Запрос на добавления фильма");
        validationFilm(film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Фильм добавлен");
        return film;
    }


    /**
     * Проверяет данные, при успешной проверке обновляет фильм
     *
     * @param film объект фильма
     * @return обновленный объект фильма
     */
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new FilmNotFoundException(String.format("Фильма с {ID} нет", id));
        }
        validationFilm(film);
        films.put(film.getId(), film);
        log.info("Фильм обновлен");
        return film;
    }

    /**
     * Возвращает все добавленные фильмы
     *
     * @return добавленные фильмы
     */
    public Collection<Film> findAll() {
        return films.values();
    }

    /**
     * поиск фильма по id
     *
     * @param id id фильма
     * @return фильм
     */
    public Film findById(Long id) {
        if (!films.containsKey(id)) {
            log.warn("Фильм с ID {} не найден", id);
            throw new FilmNotFoundException(String.format("Фильма с {ID} нет", id));
        }
        return films.get(id);
    }
}
