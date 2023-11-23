package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
	private Long id;
	private String name;
	private String description;
	@JsonProperty("available")
	private final Boolean available;
	private Long requestId;
	private final BookingDto lastBooking;
	private final BookingDto nextBooking;
	private final List<CommentDto> comments;
}
