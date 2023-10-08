package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MPAStorage;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utilites.Validation.*;

@Service
public class MPAService {
    private final MPAStorage mpaStorage;

    @Autowired
    public MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MPA findMPAById(Long id) {
        Optional<MPA> mpa = mpaStorage.findMPAById(id);
        checkMPAExists(mpa);
        return mpa.get();
    }

    public Collection<MPA> findAll() {
        return mpaStorage.findAll();
    }
}
