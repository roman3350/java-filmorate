package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenryStorage {

    Optional<Genre> findGenreById(Long id);

    Collection<Genre> findAll();
}
