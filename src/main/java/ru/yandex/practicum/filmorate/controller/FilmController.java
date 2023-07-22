package ru.yandex.practicum.filmorate.controller;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("название фильма пустое или с пробелом");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Описание длиннее 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза раньше 28.12.1895");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.warn("Продолжительность отрицательная");
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
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
        log.info("Запрос на обновления фильма");
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("название фильма пустое или с пробелом");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Описание длиннее 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза раньше 28.12.1895");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.warn("Продолжительность отрицательная");
            throw new ValidationException("продолжительность фильма должна быть положительной");
        }
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
