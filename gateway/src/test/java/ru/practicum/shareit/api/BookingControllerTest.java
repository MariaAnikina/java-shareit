package ru.practicum.shareit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.model.dto.BookingDtoDefault;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    @MockBean
    private BookingClient bookingClient;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final BookingDtoDefault bookingDtoDefault = new BookingDtoDefault(
            1,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            1,
            null,
            null
    );

    private final ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

    @Test
    public void shouldAddBooking() throws Exception {
        Mockito
                .when(bookingClient.addBooking(anyInt(), any(BookingDtoDefault.class)))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoDefault))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotAddBookingWhenStartNull() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(
                                new BookingDtoDefault(
                                        1,
                                        null,
                                        LocalDateTime.now().plusDays(2),
                                        1,
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
                                new BookingDtoDefault(
                                        1,
                                        LocalDateTime.now().minusDays(2),
                                        LocalDateTime.now().plusDays(2),
                                        1,
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

    @Test
    public void shouldNotAddBookingWhenEndNull() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(
                                new BookingDtoDefault(
                                        1,
                                        LocalDateTime.now().plusDays(1),
                                        null,
                                        1,
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

    @Test
    public void shouldNotAddBookingWhenEndBeforeNow() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(
                                new BookingDtoDefault(
                                        1,
                                        LocalDateTime.now().plusDays(1),
                                        LocalDateTime.now().minusDays(2),
                                        1,
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
                                new BookingDtoDefault(
                                        1,
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

    @Test
    public void shouldApproveBooking() throws Exception {
        Mockito
                .when(bookingClient.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(response);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetBookingById() throws Exception {
        Mockito
                .when(bookingClient.getById(anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetUserBookings() throws Exception {
        Mockito
                .when(bookingClient.getUserBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetOwnerBookings() throws Exception {
        Mockito
                .when(bookingClient.getOwnerBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}