package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Getter
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public final List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User findUserById(Integer id) {
        validateUserId(id);
        return userStorage.findUserById(id);
    }

    public User update(User user) {
        validateUser(user);
        return userStorage.update(user);
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public void addFriend(Integer user, Integer friend) {
        validateUserId(user);
        validateUserId(friend);
        User[] users = initializeUsers(user, friend);
        User userObj = users[0];
        User friendObj = users[1];
        userObj.getFriends().add(friend);
        friendObj.getFriends().add(user);
    }

    public void removeFriend(Integer user, Integer friend) {
        validateUserId(user);
        validateUserId(friend);
        if (userStorage.getUserFriends(user).isEmpty()) {
            return;
        }
        User[] users = initializeUsers(user, friend);
        User userObj = users[0];
        User friendObj = users[1];
        if (!userStorage.findUserById(user).getFriends().contains(friend)) {
            throw new NotFoundException(userStorage.findUserById(friend));
        }
        userObj.getFriends().remove(friend);
        friendObj.getFriends().remove(user);
    }

    public List<User> getCommonFriends(Integer user, Integer friend) {
        validateUserId(user);
        validateUserId(friend);
        return userStorage.getCommonFriends(user, friend);
    }

    private User[] initializeUsers(Integer userId, Integer friendId) {
        User userObj = userStorage.findUserById(userId);
        User friendObj = userStorage.findUserById(friendId);

        if (userObj == null) {
            throw new NotFoundException(userObj);
        }
        if (friendObj == null) {
            throw new NotFoundException(friendObj);
        }
        if (userObj.getFriends() == null) {
            userObj.setFriends(new HashSet<>());
        }
        if (friendObj.getFriends() == null) {
            friendObj.setFriends(new HashSet<>());
        }
        return new User[]{userObj, friendObj};
    }

    private void validateUser(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации: электронная почта не может быть пустой и должна содержать символ @.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getLogin().isBlank() || user.getLogin().matches(".*\\s+.*")) {
            log.error("Ошибка валидации: логин не может быть пустым и содержать пробелы.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
    private void validateUserId(Integer id) {
        if (id <= 0) {
            throw new EmptyIdException();
        }
    }
}