package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Map<Long, User> getUsers();

    List<User> getAllUsers();

    List<User> getUserFriends(Long id);

    void userExist(Long id);

    User findUserById(Long id);

    List<User> getCommonFriends(Long user, Long friend);

    void addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

}
