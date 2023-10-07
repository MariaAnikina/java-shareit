package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUsersById(Long id) {
        return userStorage.getUsersById(id);
    }

    public User create(User user) {
        createValidate(user);
        return userStorage.create(user);
    }

    public User update(Long id, User user) {
        updateValidate(user);
        User user1 = userStorage.getUsersMap().get(id);
        if (user.getName() != null){
            user1.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (userStorage.getUsersMap().values().stream()
                    .filter(user2 -> user.getEmail().equals(user2.getEmail()))
                    .collect(Collectors.toList()).size() == 0
            || user.getEmail().equals(user1.getEmail())) {
                user1.setEmail(user.getEmail());
            } else {
                throw new UserAlreadyExistsException("Пользователь c email: " + user.getEmail() + " существует.");
            }
        }
        return userStorage.update(id, user1);
    }

    public User delete(long id) {
        return userStorage.delete(id);
    }

    private void createValidate(User user) {
        List<User> userDuplicate = userStorage.getUsers().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
        if (userDuplicate.size() != 0) {
            throw new UserAlreadyExistsException("Пользователь с email: " + user.getEmail() + " уже существует.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный или пустой email пользователя.");
        }
        if (user.getName() == null || user.getName().isBlank()) throw new ValidationException("Имя не должно быть пустым.");
    }

    private void updateValidate(User user) {
        if (user.getEmail() != null && !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный или пустой email пользователя.");
        }
    }
}

