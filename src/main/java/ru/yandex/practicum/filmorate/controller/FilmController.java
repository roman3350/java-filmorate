package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Принимает из пути id и выводит фильм по этому id
     *
     * @param id id фильма
     * @return фильм по id
     */

    @GetMapping("/{id}")
    public Optional<Film> findFilmById(@PathVariable Long id) {
        return filmService.findFilmById(id);
    }

    /**
     * Принимает из тела запроса фильм и добавляет его
     *
     * @param film объект фильма
     * @return добавленный объект фильма
     */

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    /**
     * Принимает из тела запроса фильм и обновляет его
     *
     * @param film объект фильма
     * @return обновленный фильм
     */

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    /**
     * Вывод всех добавленные фильмы
     *
     * @return список всех фильмов
     */

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    /**
     * Добавляет лайк фильму, из пути берет id фильма и id пользователя ставящего лайк
     *
     * @param id     id фильма
     * @param userId id пользователя
     * @return лайкнутый фильм
     */

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    /**
     * удаляет лайк пользователя с фильма
     *
     * @param id     id фильма
     * @param userId id пользователя
     * @return фильм с которого удалили лайк
     */

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLike(id, userId);
    }

    /**
     * Выводит фильмы по популярности
     *
     * @param count необязательный параметр количества фильмов на вывод
     * @return фильмы по популярности
     */

    @GetMapping("/popular")
    public Collection<Film> getPopularFilm(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getFilmQuantityLike(count);
    }
}
