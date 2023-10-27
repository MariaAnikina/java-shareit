package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;

	public Collection<UserDto> getUsers() {
		return userRepository.findAll().stream().map(UserMapper::toItemDto).collect(Collectors.toList());
	}

	public UserDto getUsersById(Long id) {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
		User user = userOptional.get();
		return UserMapper.toItemDto(userRepository.getReferenceById(id));
	}

	public UserDto create(UserDto userDto) {
		try {
			User user = UserMapper.toItem(userDto);
			createValidate(user);
			userRepository.save(user);
			log.info("Добавлен пользователь {}", user);
			return UserMapper.toItemDto(user);
		} catch (ValidationException e) {
			throw new ValidationException("Email должен быть заполнен.");
		} catch (UserAlreadyExistsException e) {
			throw new UserAlreadyExistsException("Пользователь с email '" + userDto.getEmail() + "' уже существует");
		}
	}

	public UserDto update(Long id, UserDto userDto) {
		User user = userRepository.getReferenceById(id);
		if (userDto.getName() != null) {
			user.setName(userDto.getName());
		}
		if (userDto.getEmail() != null) {
			user.setEmail(userDto.getEmail());
		}
		return UserMapper.toItemDto(userRepository.save(user));
	}

	public void delete(long id) {
		userRepository.deleteById(id);
	}

	private void createValidate(User user) {
		updateValidate(user);
		if (user.getName() == null || user.getName().isBlank())
			throw new ValidationException("Имя не должно быть пустым.");
		if (user.getEmail() == null) throw new ValidationException("Email должен быть заполнен.");
	}

	private void updateValidate(User user) {
		if (user.getEmail() != null && !user.getEmail().contains("@")) {
			throw new ValidationException("Некорректный или пустой email пользователя.");
		}
	}
}

