package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utilites.Validation.validationFilm;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int id;

    /**
     * Проверяет данные, при успешной проверке добавляет объект фильма в мапу
     *
     * @param film объект фильма
     * @return добавленный объект фильма
     */
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
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
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new ValidationException("Фильма с таким ID нет");
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
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }
}
