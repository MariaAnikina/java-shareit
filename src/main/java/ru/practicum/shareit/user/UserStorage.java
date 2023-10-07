package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    Collection<User> getUsers();

    User getUsersById(Long id);

    User create(User user);

    User update(Long id, User user);


    User delete(Long id);

    Map<Long, User> getUsersMap();
}
