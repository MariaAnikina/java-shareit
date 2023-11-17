package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GetYourRequestsTest {
	private final EntityManager em;
	private final ItemRequestService itemRequestService;
	private final UserService userService;

	@Test
	public void shouldGetItemRequestsByUserId() {
		ItemRequestDto itemRequestDto = new ItemRequestDto(
				null,
				"Test description",
				LocalDateTime.now()
		);
		UserDto userDto = new UserDto(null, "user1", "user1@email.com");
		userService.create(userDto);
		TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
		User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
		Long userId = user.getId();
		itemRequestService.create(userId, itemRequestDto);
		List<ItemRequestOutDto> itemRequests = itemRequestService.getYourRequests(userId);
		ItemRequestOutDto itemRequestDtoOutgoing = itemRequests.get(0);

		assertThat(1, equalTo(itemRequests.size()));
		assertThat(itemRequestDtoOutgoing.getId(), notNullValue());
		assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
		assertThat(itemRequestDtoOutgoing.getItems(), equalTo(Collections.emptyList()));
	}

	@Test
	public void shouldNotGetItemRequestsByUserIdWhenUserNotFound() {
		UserNotFoundException e = Assertions.assertThrows(
				UserNotFoundException.class,
				() -> itemRequestService.getYourRequests(1L)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id: 1 не найден"));
	}
}
