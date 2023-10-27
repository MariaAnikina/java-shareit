package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
	private Long id;
	@NotBlank
	private String name;
	@NotBlank
	private String description;
	@JsonProperty("available")
	@NotNull(message = "Должна быть указана доступность вещи")
	private final Boolean available;
	private Long request;
	private final BookingDto lastBooking;
	private final BookingDto nextBooking;
	private final List<CommentDto> comments;
}
