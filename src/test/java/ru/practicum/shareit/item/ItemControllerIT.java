package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerIT {
	@MockBean
	private ItemService itemService;
	private final ObjectMapper mapper;
	private final MockMvc mvc;

	private final ItemDto itemDto = new ItemDto(
			1L,
			"item1",
			"item1 description",
			true,
			null,
			null,
			null,
			Collections.emptyList()
	);

	@SneakyThrows
	@Test
	public void shouldGetItemById() {
		when(itemService.getItemById(anyLong(), anyLong()))
				.thenReturn(itemDto);

		mvc.perform(get("/items/1")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(itemDto.getId()))
				.andExpect(jsonPath("$.name").value(itemDto.getName()))
				.andExpect(jsonPath("$.description").value(itemDto.getDescription()));
	}

	@SneakyThrows
	@Test
	public void shouldGetItemsByUserId() {
		when(itemService.getItemsUser(anyLong(), anyInt(), anyInt()))
				.thenReturn(List.of(itemDto));

		mvc.perform(get("/items")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
				.andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
				.andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()));
	}

	@SneakyThrows
	@Test
	public void shouldAddItem() {
		when(itemService.create(anyLong(), any(ItemDto.class)))
				.thenReturn(itemDto);

		mvc.perform(post("/items")
						.content(mapper.writeValueAsString(itemDto))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(itemDto.getId()))
				.andExpect(jsonPath("$.name").value(itemDto.getName()))
				.andExpect(jsonPath("$.description").value(itemDto.getDescription()));
	}

	@SneakyThrows
	@Test
	public void shouldNotAddItemWhenBlankName() {
		mvc.perform(post("/items")
						.content(mapper.writeValueAsString(
								new ItemDto(
										1L,
										"",
										"item1 description",
										true,
										null,
										null,
										null,
										Collections.emptyList()
								)
						))
						.header("X-Sharer-User-Id", 1L)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Имя вещи не может быть пустым"));
	}

	@SneakyThrows
	@Test
	public void shouldNotAddItemWhenBlankDescription() {
		mvc.perform(post("/items")
						.content(mapper.writeValueAsString(
								new ItemDto(
										1L,
										"item1",
										"",
										true,
										null,
										null,
										null,
										Collections.emptyList()
								)
						))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Описание вещи не может быть пустым"));
	}

	@SneakyThrows
	@Test
	public void shouldNotAddItemWhenIsAvailableNull() throws Exception {
		mvc.perform(post("/items")
						.content(mapper.writeValueAsString(
								new ItemDto(
										1L,
										"item1",
										"item1 description",
										null,
										null,
										null,
										null,
										Collections.emptyList()
								)
						))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Должна быть указана доступность вещи"));
	}

	@SneakyThrows
	@Test
	public void shouldUpdateItem() {
		when(itemService.update(anyLong(), anyLong(), any(ItemDto.class)))
				.thenReturn(itemDto);

		mvc.perform(patch("/items/1")
						.content(mapper.writeValueAsString(itemDto))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(itemDto.getId()))
				.andExpect(jsonPath("$.name").value(itemDto.getName()))
				.andExpect(jsonPath("$.description").value(itemDto.getDescription()));
	}

	@SneakyThrows
	@Test
	public void shouldFindItems() {
		when(itemService.getItemsByNameOrDescription(anyLong(), anyString(), anyInt(), anyInt()))
				.thenReturn(List.of(itemDto));

		mvc.perform(get("/items/search")
						.header("X-Sharer-User-Id", 1)
						.param("text", "item")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
				.andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
				.andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()));
	}

	@SneakyThrows
	@Test
	public void shouldAddComment() {
		CommentDto commentDto = new CommentDto(
				1L,
				"test comment text",
				"user1",
				LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
		);
		when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
				.thenReturn(commentDto);

		mvc.perform(post("/items/1/comment")
						.content(mapper.writeValueAsString(commentDto))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(commentDto.getId()))
				.andExpect(jsonPath("$.text").value(commentDto.getText()))
				.andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
				.andExpect(jsonPath("$.created").value(commentDto.getCreated().toString()));
	}

	@SneakyThrows
	@Test
	public void shouldNotAddCommentWhenBlankText() {
		CommentDto commentDto = new CommentDto(
				1L,
				"",
				"user1",
				LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
		);

		mvc.perform(post("/items/1/comment")
						.content(mapper.writeValueAsString(commentDto))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Текст комментария не может быть пустым"));
	}
}
