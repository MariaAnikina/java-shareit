package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage {
    private Map<Long,User> users = new HashMap<>();
    private Long lastId = 0L;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUsersById(Long id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(Long id, User user) {
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User delete(Long id) {
        return users.remove(id);
    }

    private Long getId() {
        return ++lastId;
    }

    @Override
    public Map<Long, User> getUsersMap() {
        return users;
    }
}
