package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemRequestMapper {
	public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
		return new ItemRequestDto(
				itemRequest.getId(),
				itemRequest.getDescription(),
				itemRequest.getCreated()
		);
	}

	public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
		return new ItemRequest(
				itemRequestDto.getId(),
				itemRequestDto.getDescription(),
				user,
				itemRequestDto.getCreated()
		);
	}

	public static ItemRequestOutDto toItemRequestOutDto(ItemRequest itemRequest, List<ItemDto> items) {
		return new ItemRequestOutDto(
				itemRequest.getId(),
				itemRequest.getDescription(),
				itemRequest.getCreated(),
				items
		);
	}
}
