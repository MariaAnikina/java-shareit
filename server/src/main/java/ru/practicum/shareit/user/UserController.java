package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
	private final UserService service;

	@GetMapping("/{id}")
	public UserDto getUsersById(@PathVariable Long id) {
		return service.getUsersById(id);
	}

	@GetMapping
	public Collection<UserDto> getUsers() {
		return service.getUsers();
	}

	@PostMapping
	public UserDto create(@RequestBody UserDto userDto) {
		return service.create(userDto);
	}

	@PatchMapping("/{id}")
	public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
		return service.update(id, userDto);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable long id) {
		service.delete(id);
	}
}
