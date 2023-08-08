package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@SpringBootTest
public class FilmTest {
    FilmController filmController;
    @Autowired
    FilmService filmService;
    @Autowired
    FilmStorage filmStorage;

    @BeforeEach
    void createController() {
        filmController = new FilmController(filmService, filmStorage);
    }

    @Test
    void createFilm() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 03, 25))
                .duration(100)
                .build();
        Film filmSave = filmController.create(film);
        assertEquals(film, filmSave);
    }

    @Test
    void createFailFailNameNull() {
        Film film = Film.builder()
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 03, 25))
                .duration(100)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        }, "название не может быть пустым");
    }

    @Test
    void createFilmFailNameSpace() {
        Film film = Film.builder()
                .name("")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 03, 25))
                .duration(100)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        }, "название не может быть пустым");
    }

    @Test
    void createFilmFailDescription() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, " +
                        "который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                        "который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(1967, 03, 25))
                .duration(100)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        }, "максимальная длина описания — 200 символов");
    }

    @Test
    void createFilmDescription200char() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, " +
                        "который задолжал им день")
                .releaseDate(LocalDate.of(1967, 03, 25))
                .duration(100)
                .build();
        Film filmSave = filmController.create(film);
        assertEquals(film, filmSave);
    }

    @Test
    void createFilmFailreleaseDate() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("Description")
                .releaseDate(LocalDate.of(1890, 03, 25))
                .duration(100)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        }, "дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    void createFilmFailreleaseDateBefore() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28).minus(1, ChronoUnit.DAYS))
                .duration(100)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        }, "дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    void createFilmFailreleaseDateAfter() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28).plus(1, ChronoUnit.DAYS))
                .duration(100)
                .build();
        Film filmSave = filmController.create(film);
        assertEquals(film, filmSave);
    }

    @Test
    void createFilmFailDuration() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("Description")
                .releaseDate(LocalDate.of(1990, 03, 25))
                .duration(-1)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        }, "продолжительность фильма должна быть положительной");
    }

    @Test
    void updateFilm() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("Description")
                .releaseDate(LocalDate.of(1990, 03, 25))
                .duration(100)
                .build();
        Film filmSave = filmController.create(film);
        film = Film.builder()
                .id(1L)
                .name("Film Updated")
                .description("New film update decription")
                .releaseDate(LocalDate.of(1989, 04, 17))
                .duration(100)
                .build();
        Film filmUpdate = filmController.update(film);
        assertEquals(film, filmUpdate);
        assertNotEquals(filmSave, filmUpdate);
    }

    @Test
    void updateFilmUnknown() {
        Film film = Film.builder()
                .id(9999L)
                .name("nisi eiusmod")
                .description("Description")
                .releaseDate(LocalDate.of(1990, 03, 25))
                .duration(100)
                .build();
        assertThrows(FilmNotFoundException.class, () -> {
            filmController.update(film);
        }, "Фильм с ID 9999 не найден");
    }

}
