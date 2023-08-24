package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface MPAStorage {

    Optional<MPA> findMPAById(Long id);

    Collection<MPA> findAll();
}
