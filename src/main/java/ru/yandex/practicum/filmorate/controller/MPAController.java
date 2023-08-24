package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MPAService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MPAController {

    private final MPAService mpaService;

    public MPAController(MPAService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping()
    public Collection<MPA> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public MPA findMPAById(@PathVariable Long id) {
        return mpaService.findMPAById(id);
    }
}
