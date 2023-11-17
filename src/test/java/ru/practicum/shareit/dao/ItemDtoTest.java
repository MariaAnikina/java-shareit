package ru.practicum.shareit.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoTest {
	private final JacksonTester<ItemDto> json;

	@Test
	void testUserDto() throws Exception {
		ItemDto itemDto = new ItemDto(
				1L,
				"item",
				"item description",
				true,
				null,
				null,
				null,
				Collections.emptyList()
		);
		JsonContent<ItemDto> result = json.write(itemDto);

		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item description");
		assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
	}
}