package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;
	private final UserDto userDto = new UserDto(1L, "test", "test@mail.com");

	@SneakyThrows
	@Test
	void shouldGetUsersById() {
		when(userService.getUsersById(userDto.getId())).thenReturn(userDto);
		mvc.perform(get("/users/{id}", userDto.getId()))
				.andDo(print())
				.andExpect(status().isOk());

		verify(userService, times(1)).getUsersById(userDto.getId());
	}

	@SneakyThrows
	@Test
	public void shouldGetUsers() {
		when(userService.getUsers()).thenReturn(List.of(userDto));

		mvc.perform(get("/users")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$.[0].id").value(userDto.getId()))
				.andExpect(jsonPath("$.[0].name").value(userDto.getName()))
				.andExpect(jsonPath("$.[0].email").value(userDto.getEmail()));
	}

	@SneakyThrows
	@Test
	public void shouldAddUser() {
		when(userService.create(any(UserDto.class))).then(returnsFirstArg());

		mvc.perform(post("/users")
						.content(mapper.writeValueAsString(userDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(userDto.getId()))
				.andExpect(jsonPath("$.name").value(userDto.getName()))
				.andExpect(jsonPath("$.email").value(userDto.getEmail()));
	}

	@SneakyThrows
	@Test
	public void shouldNotCreateUserWhenBlankName() {
		mvc.perform(post("/users")
						.content(mapper.writeValueAsString(new UserDto(1L, "", "test@mail.com")))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Имя пользователя не может быть пустым"));
	}

	@SneakyThrows
	@Test
	public void shouldNotCreateUserWhenBlankEmail() {
		mvc.perform(post("/users")
						.content(mapper.writeValueAsString(new UserDto(1L, "test", "")))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Email пользователя не может быть пустым"));
	}

	@SneakyThrows
	@Test
	public void shouldNotAddUserWhenIncorrectEmail() throws Exception {
		mvc.perform(post("/users")
						.content(mapper.writeValueAsString(new UserDto(1L, "test", "email.com")))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error")
						.value("Email пользователя должен быть корректным"));
	}

	@SneakyThrows
	@Test
	public void shouldUpdateUser() {
		when(userService.update(anyLong(), any(UserDto.class))).then(returnsArgAt(1));

		mvc.perform(patch("/users/1")
						.content(mapper.writeValueAsString(userDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(userDto.getId()))
				.andExpect(jsonPath("$.name").value(userDto.getName()))
				.andExpect(jsonPath("$.email").value(userDto.getEmail()));
	}
}