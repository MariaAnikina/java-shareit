package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("/{id}")
    public UserDto getUsersById(@PathVariable Long id) {
        return UserMapper.toItemDto(service.getUsersById(id));
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        return service.getUsers().stream()
                .map(UserMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return service.create(user);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        return service.update(id, user);
    }

    @DeleteMapping("/{id}")
    public User delete(@PathVariable long id) {
        return service.delete(id);
    }
}
