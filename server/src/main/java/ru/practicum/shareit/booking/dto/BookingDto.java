package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
	private Long id;
	private final LocalDateTime start;
	private final LocalDateTime end;
	private final Long itemId;
	private Long bookerId;
	private Status status;
}
