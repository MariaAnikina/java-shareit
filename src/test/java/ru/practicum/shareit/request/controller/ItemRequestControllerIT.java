package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerIT {
	@MockBean
	private ItemRequestService itemRequestService;
	private final ObjectMapper mapper;
	private final MockMvc mvc;

	private final ItemRequestDto itemRequestDto = new ItemRequestDto(
			1L,
			"item request description",
			LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
	);

	@SneakyThrows
	@Test
	public void shouldAddItemRequest() {
		when(itemRequestService.create(anyLong(), any(ItemRequestDto.class)))
				.thenReturn(itemRequestDto);

		mvc.perform(post("/requests")
						.content(mapper.writeValueAsString(itemRequestDto))
						.header("X-Sharer-User-Id", 1L)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
				.andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
				.andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().toString()));
	}

	@Test
	public void shouldNotAddItemRequestWhenBlankDescription() throws Exception {
		mvc.perform(post("/requests")
						.content(mapper.writeValueAsString(
								new ItemRequestDto(
										1L,
										"",
										LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
								)
						))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Описание запроса не может быть пустым"));
	}

	@Test
	public void shouldGetItemRequestsByUserId() throws Exception {
		ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto(1L,
				"item request description",
				LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
				new ArrayList<>());
		when(itemRequestService.getYourRequests(anyLong()))
				.thenReturn(List.of(itemRequestOutDto));

		mvc.perform(get("/requests")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1L))
				.andExpect(jsonPath("$.[0].id").value(itemRequestOutDto.getId()))
				.andExpect(jsonPath("$.[0].description").value(itemRequestOutDto.getDescription()))
				.andExpect(jsonPath("$.[0].created").value(itemRequestOutDto.getCreated().toString()));
	}

	@Test
	public void shouldGetAllItemRequests() throws Exception {
		ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto(1L,
				"item request description",
				LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
				new ArrayList<>());
		when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
				.thenReturn(List.of(itemRequestOutDto));

		mvc.perform(get("/requests/all")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$.[0].id").value(itemRequestOutDto.getId()))
				.andExpect(jsonPath("$.[0].description").value(itemRequestOutDto.getDescription()))
				.andExpect(jsonPath("$.[0].created").value(itemRequestOutDto.getCreated().toString()));
	}

	@Test
	public void shouldGetItemRequestById() throws Exception {
		ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto(1L,
				"item request description",
				LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
				new ArrayList<>());
		when(itemRequestService.getByIdRequest(anyLong(), anyLong()))
				.thenReturn(itemRequestOutDto);

		mvc.perform(get("/requests/1")
						.header("X-Sharer-User-Id", 1L)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(itemRequestOutDto.getId()))
				.andExpect(jsonPath("$.description").value(itemRequestOutDto.getDescription()))
				.andExpect(jsonPath("$.created").value(itemRequestOutDto.getCreated().toString()));
	}
}