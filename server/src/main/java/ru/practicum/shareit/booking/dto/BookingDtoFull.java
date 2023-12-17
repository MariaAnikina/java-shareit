package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDtoFull {
	private final Long id;
	private final LocalDateTime start;
	private final LocalDateTime end;
	private final ItemDto item;
	private final UserDto booker;
	private final Status status;
}
