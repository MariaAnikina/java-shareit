package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GetItemsUserTest {
	private final EntityManager em;
	private final ItemService itemService;
	private final UserService userService;

	@Test
	public void shouldGetItemsByUserId() {
		ItemDto itemDto = new ItemDto(
				null,
				"Test name",
				"Test description",
				true,
				null,
				null,
				null,
				null
		);
		UserDto userDto = new UserDto(null, "user1", "user1@email.com");
		userService.create(userDto);
		TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
		User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
		Long userId = user.getId();
		itemService.create(userId, itemDto);
		List<ItemDto> items = (List<ItemDto>) itemService.getItemsUser(userId, 0, 5);
		ItemDto itemDtoOutgoing = items.get(0);

		assertThat(items.size(), equalTo(1));
		assertThat(itemDtoOutgoing.getId(), notNullValue());
		assertThat(itemDtoOutgoing.getName(), equalTo(itemDto.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(itemDto.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(itemDto.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), nullValue());
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
		assertThat(itemDtoOutgoing.getComments(), equalTo(Collections.emptyList()));
	}

	@Test
	public void shouldNotGetItemsByUserIdWhenUserNotFound() {
		UserNotFoundException e = Assertions.assertThrows(
				UserNotFoundException.class,
				() -> itemService.getItemsUser(1L, 0, 5)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}
}
