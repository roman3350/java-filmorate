package ru.yandex.practicum.filmorate.utilites;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
public class Validation {

    public static void validationUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("электронная почта пустая или не содержит символ @");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("логин пустой или содержит пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем ");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
    }

    public static void validationFilm(Film film) {
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
    }

    public static void checkUserExists(Optional<User> user) {
        if (user.isEmpty()) {
            log.warn("Пользователь не найден в БД");
            throw new UserNotFoundException("Пользователя не найден");
        }
    }

    public static void checkGenreExists(Optional<Genre> genre) {
        if (genre.isEmpty()) {
            log.warn("Жанр не найден в БД");
            throw new GenreNotFoundException("Жанр не найден");
        }
    }

    public static void checkMPAExists(Optional<MPA> mpa) {
        if (mpa.isEmpty()) {
            log.warn("Рейтинг не найден в БД");
            throw new MPANotFoundException("Рейтинга не найден");
        }
    }

    public static void checkFriendExists(Boolean confirmFriend) {
        if (!confirmFriend) {
            log.warn("Пользотваель не найден, либо он уже добавлен в друзья");
            throw new UserNotFoundException("Запрос на добавления в друзья нет, либо он уже в друзьях");
        }
    }

}
