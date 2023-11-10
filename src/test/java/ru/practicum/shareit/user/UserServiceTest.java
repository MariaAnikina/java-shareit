package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	void getUsers_whenUsersFound_thenReturnUsers() {
		User user1 = new User(1L, "Ваня", "Van@mail.ru");
		User user2 = new User(2L, "Аня", "Anna@mail.ru");
		when(userRepository.findAll()).thenReturn(List.of(user1, user2));

		Collection<UserDto> users = userService.getUsers();

		assertEquals(users.size(), 2);
		assertTrue(users.contains(UserMapper.toItemDto(user1)));
		assertTrue(users.contains(UserMapper.toItemDto(user2)));
	}

	@Test
	void getUsersById_whenUserFound_thenReturnUser() {
		User user1 = new User(1L, "Ваня", "Van@mail.ru");
		User user2 = new User(2L, "Аня", "Anna@mail.ru");
		when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

		UserDto userDto = userService.getUsersById(2L);

		assertEquals(UserMapper.toItem(userDto), user2);
	}

	@Test
	void getUsersById_whenUserNotFound_thenReturnUserNotFoundException() {
		User user1 = new User(1L, "Ваня", "Van@mail.ru");
		User user2 = new User(2L, "Аня", "Anna@mail.ru");
		when(userRepository.findById(2L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getUsersById(2L));
	}

	@Test
	void create_whenUserCreate_thenReturnUser() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		when(userRepository.save(user)).thenReturn(user);

		UserDto userDto = userService.create(UserMapper.toItemDto(user));

		assertEquals(user, UserMapper.toItem(userDto));
	}

	@Test
	void create_whenUserNotValidEmail_thenReturnValidationException() {
		User user = new User(1L, "Ваня", " ");

		assertThrows(ValidationException.class, () -> userService.create(UserMapper.toItemDto(user)));
	}

	@Test
	void create_whenUserNotValidName_thenReturnValidationException() {
		User user = new User(1L, null, "Van@mail.ru");

		assertThrows(ValidationException.class, () -> userService.create(UserMapper.toItemDto(user)));
	}

	@Test
	void update_whenUserFound_thenReturnUser() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User userUpdate = new User(1L, "Больше не Ваня", null);
		User userUpdateSave = new User(1L, "Больше не Ваня", "Van@mail.ru");
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(userRepository.save(userUpdateSave)).thenReturn(userUpdateSave);

		userService.create(UserMapper.toItemDto(user));
		UserDto userUpdateDto = userService.update(user.getId(), UserMapper.toItemDto(userUpdate));


		assertEquals(userUpdateDto.getId(), 1L);
		assertEquals(userUpdateDto.getName(), userUpdate.getName());
		assertEquals(userUpdateDto.getEmail(), user.getEmail());
	}

	@Test
	void update_whenUserNotFound_thenReturnUserNotFoundException() {
		User userUpdate = new User(1L, "Больше не Ваня", null);
		UserDto userUpdateDto = UserMapper.toItemDto(userUpdate);

		assertThrows(UserNotFoundException.class, () -> userService.update(1L, userUpdateDto));
	}
}