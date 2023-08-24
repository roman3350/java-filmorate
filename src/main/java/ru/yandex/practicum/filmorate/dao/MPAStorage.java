package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.Optional;

public interface MPAStorage {

    Optional<MPA> findMPAById(Long id);

    Collection<MPA> findAll();
}
