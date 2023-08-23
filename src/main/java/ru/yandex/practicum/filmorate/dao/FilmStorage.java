package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Optional<Film> findFilmById(Long id);

    Film addLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    Collection<Film> getFilmQuantityLike(int count);
}