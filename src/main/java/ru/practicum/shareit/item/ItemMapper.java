package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
	public static ItemDto toItemDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getAvailable(),
				item.getRequestId(),
				lastBooking,
				nextBooking,
				comments
		);
	}

	public static ItemDto toItemDto(Item item, List<CommentDto> comments) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getAvailable(),
				item.getRequestId(),
				null,
				null,
				comments
		);
	}

	public static Item toItem(ItemDto itemDto) {
		return new Item(
				itemDto.getId(),
				itemDto.getName(),
				itemDto.getDescription(),
				itemDto.getAvailable(),
				null,
				itemDto.getRequestId()
		);
	}

	public static Item toItem(ItemDto itemDto, User user) {
		return new Item(
				null,
				itemDto.getName(),
				itemDto.getDescription(),
				itemDto.getAvailable() != null ? itemDto.getAvailable() : null,
				user,
				itemDto.getRequestId()
		);
	}
}
