package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDtoFull {
	private final Long id;
	@NotNull(message = "Поле start не должно быть null")
	@Future(message = "Поле start должно содержать дату, которая еще не наступила")
	private final LocalDateTime start;
	@NotNull(message = "Поле end не должно быть null")
	@Future(message = "Поле end должно содержать дату, которая еще не наступила")
	private final LocalDateTime end;
	private final ItemDto item;
	private final UserDto booker;
	private final Status status;
}
