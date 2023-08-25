package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserTest {
    UserController userController;
    @Autowired
    UserService userService;

    @BeforeEach
    void createController() {
        userController = new UserController(userService);
    }

    @Test
    void createUser() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        User userSave = userController.create(user);
        assertEquals(userController.findUserById(3L), userSave);
    }

    @Test
    void createUserFailLoginNull() {
        User user = User.builder()
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        }, "логин не может быть пустым и содержать пробелы");
    }

    @Test
    void createUserFailLoginSpace() {
        User user = User.builder()
                .login(" ")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        }, "логин не может быть пустым и содержать пробелы");
    }

    @Test
    void createUserFailEmailNull() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        }, "электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void createUserFailEmailSpacer() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email(" ")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        }, "электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void createUserFailEmailNotContainsDog() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        }, "электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void createUserFailBirthday() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2446, 8, 20))
                .build();
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        }, "дата рождения не может быть в будущем");
    }

    @Test
    void createUserBirthdayBefore() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.now().minus(1, ChronoUnit.DAYS))
                .build();
        User userSave = userController.create(user);
        assertEquals(userController.findUserById(3L), userSave);
    }

    @Test
    void createUserFailBirthdayAfter() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.now().plus(1, ChronoUnit.DAYS))
                .build();
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        }, "дата рождения не может быть в будущем");
    }

    @Test
    void updateUser() {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        User userSave = userController.create(user);
        user = User.builder()
                .id(1L)
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1976, 9, 20))
                .build();
        User userUpdate = userController.update(user);
        assertEquals(user, userUpdate);
        assertNotEquals(userSave, userUpdate);
    }

    @Test
    void updateUserUnknown() {
        User user = User.builder()
                .id(9999L)
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1976, 9, 20))
                .build();
        assertThrows(UserNotFoundException.class, () -> {
            userController.update(user);
        }, "Пользователя с таким ID нет");
    }

}
