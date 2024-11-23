package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void remove(int id);

    Map<Long, User> getUsers();

    List<User> getAllUsers();

    List<User> getUserFriends(Integer id);

    User findUserById(Integer id);

    List<User> getCommonFriends(Integer user, Integer friend);
}
