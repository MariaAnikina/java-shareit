package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerIT {
	@MockBean
	private BookingService bookingService;
	private final ObjectMapper mapper;
	private final MockMvc mvc;

	private final BookingDto bookingDtoDefault = new BookingDto(
			1L,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(2),
			1L,
			null,
			null
	);

	private final BookingDtoFull bookingDtoOutgoing = new BookingDtoFull(
			1L,
			LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
			LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS),
			new ItemDto(
					1L,
					"item1",
					"item1 description",
					true,
					null,
					null,
					null,
					Collections.emptyList()
			),
			new UserDto(1L, "user1", "user1@email.com"),
			Status.WAITING
	);

	@SneakyThrows
	@Test
	public void shouldAddBooking() {
		when(bookingService.create(anyLong(), any(BookingDto.class)))
				.thenReturn(bookingDtoOutgoing);

		mvc.perform(post("/bookings")
						.content(mapper.writeValueAsString(bookingDtoDefault))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookingDtoOutgoing.getId()))
				.andExpect(jsonPath("$.start").value(bookingDtoOutgoing.getStart().toString()))
				.andExpect(jsonPath("$.end").value(bookingDtoOutgoing.getEnd().toString()))
				.andExpect(jsonPath("$.status").value(bookingDtoOutgoing.getStatus().toString()))
				.andExpect(jsonPath("$.item.id").value(bookingDtoOutgoing.getItem().getId()))
				.andExpect(jsonPath("$.booker.id").value(bookingDtoOutgoing.getBooker().getId()));
	}

	@SneakyThrows
	@Test
	public void shouldNotAddBookingWhenStartNull() {
		mvc.perform(post("/bookings")
						.content(mapper.writeValueAsString(
								new BookingDto(
										1L,
										null,
										LocalDateTime.now().plusDays(2),
										1L,
										null,
										null
								)))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Поле start не должно быть null"));
	}

	@Test
	public void shouldNotAddBookingWhenStartBeforeNow() throws Exception {
		mvc.perform(post("/bookings")
						.content(mapper.writeValueAsString(
								new BookingDto(
										1L,
										LocalDateTime.now().minusDays(2),
										LocalDateTime.now().plusDays(2),
										1L,
										null,
										null
								)))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error")
						.value("Поле start должно содержать дату, которая еще не наступила"));
	}

	@SneakyThrows
	@Test
	public void shouldNotAddBookingWhenEndNull() {
		mvc.perform(post("/bookings")
						.content(mapper.writeValueAsString(
								new BookingDto(
										1L,
										LocalDateTime.now().plusDays(1),
										null,
										1L,
										null,
										null
								)))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Поле end не должно быть null"));
	}

	@SneakyThrows
	@Test
	public void shouldNotAddBookingWhenEndBeforeNow() {
		mvc.perform(post("/bookings")
						.content(mapper.writeValueAsString(
								new BookingDto(
										1L,
										LocalDateTime.now().plusDays(1),
										LocalDateTime.now().minusDays(2),
										1L,
										null,
										null
								)))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error")
						.value("Поле end должно содержать дату, которая еще не наступила"));
	}

	@Test
	public void shouldNotAddBookingWhenItemIdNull() throws Exception {
		mvc.perform(post("/bookings")
						.content(mapper.writeValueAsString(
								new BookingDto(
										1L,
										LocalDateTime.now().plusDays(1),
										LocalDateTime.now().plusDays(2),
										null,
										null,
										null
								)))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Id бронируемой вещи не должно быть null"));
	}

	@SneakyThrows
	@Test
	public void shouldApproveBooking() {
		when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
				.thenReturn(bookingDtoOutgoing);

		mvc.perform(patch("/bookings/1")
						.header("X-Sharer-User-Id", 1)
						.param("approved", String.valueOf(true))
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookingDtoOutgoing.getId()))
				.andExpect(jsonPath("$.start").value(bookingDtoOutgoing.getStart().toString()))
				.andExpect(jsonPath("$.end").value(bookingDtoOutgoing.getEnd().toString()))
				.andExpect(jsonPath("$.status").value(bookingDtoOutgoing.getStatus().toString()))
				.andExpect(jsonPath("$.item.id").value(bookingDtoOutgoing.getItem().getId()))
				.andExpect(jsonPath("$.booker.id").value(bookingDtoOutgoing.getBooker().getId()));
	}

	@Test
	public void shouldGetBookingById() throws Exception {
		when(bookingService.getBookingInformation(anyLong(), anyLong()))
				.thenReturn(bookingDtoOutgoing);

		mvc.perform(get("/bookings/1")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookingDtoOutgoing.getId()))
				.andExpect(jsonPath("$.start").value(bookingDtoOutgoing.getStart().toString()))
				.andExpect(jsonPath("$.end").value(bookingDtoOutgoing.getEnd().toString()))
				.andExpect(jsonPath("$.status").value(bookingDtoOutgoing.getStatus().toString()))
				.andExpect(jsonPath("$.item.id").value(bookingDtoOutgoing.getItem().getId()))
				.andExpect(jsonPath("$.booker.id").value(bookingDtoOutgoing.getBooker().getId()));
	}

	@Test
	public void shouldGetUserBookings() throws Exception {
		when(bookingService.getBooking(anyLong(), anyString(), anyInt(), anyInt()))
				.thenReturn(List.of(bookingDtoOutgoing));

		mvc.perform(get("/bookings")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$.[0].id").value(bookingDtoOutgoing.getId()))
				.andExpect(jsonPath("$.[0].start").value(bookingDtoOutgoing.getStart().toString()))
				.andExpect(jsonPath("$.[0].end").value(bookingDtoOutgoing.getEnd().toString()))
				.andExpect(jsonPath("$.[0].status").value(bookingDtoOutgoing.getStatus().toString()))
				.andExpect(jsonPath("$.[0].item.id").value(bookingDtoOutgoing.getItem().getId()))
				.andExpect(jsonPath("$.[0].booker.id").value(bookingDtoOutgoing.getBooker().getId()));
	}

	@Test
	public void shouldGetOwnerBookings() throws Exception {
		when(bookingService.getYourBooking(anyLong(), anyString(), anyInt(), anyInt()))
				.thenReturn(List.of(bookingDtoOutgoing));

		mvc.perform(get("/bookings/owner")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$.[0].id").value(bookingDtoOutgoing.getId()))
				.andExpect(jsonPath("$.[0].start").value(bookingDtoOutgoing.getStart().toString()))
				.andExpect(jsonPath("$.[0].end").value(bookingDtoOutgoing.getEnd().toString()))
				.andExpect(jsonPath("$.[0].status").value(bookingDtoOutgoing.getStatus().toString()))
				.andExpect(jsonPath("$.[0].item.id").value(bookingDtoOutgoing.getItem().getId()))
				.andExpect(jsonPath("$.[0].booker.id").value(bookingDtoOutgoing.getBooker().getId()));
	}
}