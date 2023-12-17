package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
	private final ItemRepository itemStorage;
	private final UserRepository userStorage;

	@Test
	public void shouldFindByText() {
		User user = new User(null, "user", "user@email.com");
		userStorage.save(user);
		Item item = new Item(
				null,
				"item name",
				"item description",
				true,
				user,
				null
		);
		itemStorage.save(item);
		List<Item> items = (List<Item>) itemStorage.getItemsByNameOrDescription("item");

		assertThat(items.size(), equalTo(1));
		assertThat(items.get(0), equalTo(item));

		items = (List<Item>) itemStorage.getItemsByNameOrDescription("scissors");

		assertThat(items.size(), equalTo(0));
	}
}
