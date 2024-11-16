package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UserControllerTest {
    private UserController userController;
    private int currentId;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        currentId = 1;
    }

    private User createUser(String email, String login, String name, LocalDate birthday) {
        return User.builder()
                .id(currentId)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }

    @Test
    void emailMustBeCorrect() {
        User user = createUser("anton.anton", "anton", "anton", LocalDate.of(2012, 12, 12));
        Exception exception = assertThrows(ValidationException.class, () -> userController.validateUser(user));
        currentId++;
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
    }

    @Test
    void userLoginCannotBeEmptyOrContainSpaces() {
        String[] logins = {"", "anton anton2", " anton", "anton "};
        for (String login : logins) {
            User user = createUser("anton@gmail.com", login, "anton", LocalDate.of(2012, 12, 12));
            Exception exception = assertThrows(ValidationException.class, () -> userController.validateUser(user));
            assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
        }
    }

    @Test
    void userBirthdayMustNotBeInTheFuture() {
        User user = createUser("anton@gmail.com", "anton", "anton", LocalDate.MAX);
        Exception exception = assertThrows(ValidationException.class, () -> userController.validateUser(user));
        currentId++;
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }
}