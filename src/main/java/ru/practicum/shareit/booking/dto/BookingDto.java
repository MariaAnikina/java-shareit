package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
	private Long id;
	@NotNull(message = "Поле start не должно быть null")
	@Future(message = "Поле start должно содержать дату, которая еще не наступила")
	private final LocalDateTime start;
	@NotNull(message = "Поле end не должно быть null")
	@Future(message = "Поле end должно содержать дату, которая еще не наступила")
	private final LocalDateTime end;
	@NotNull(message = "Id бронируемой вещи не должно быть null")
	private final Long itemId;
	private Long bookerId;
	private Status status;
}
