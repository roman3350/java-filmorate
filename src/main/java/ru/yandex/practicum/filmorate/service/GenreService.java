package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenryStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utilites.Validation.checkGenreExists;

@Service
public class GenreService {
    private final GenryStorage genryStorage;

    @Autowired
    public GenreService(GenryStorage genryStorage) {
        this.genryStorage = genryStorage;
    }

    public Genre findGenreById(Long id) {
        Optional<Genre> genre = genryStorage.findGenreById(id);
        checkGenreExists(genre);
        return genre.get();
    }

    public Collection<Genre> findAll() {
        return genryStorage.findAll();
    }
}
