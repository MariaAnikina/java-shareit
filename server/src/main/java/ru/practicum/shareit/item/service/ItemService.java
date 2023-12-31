package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
	ItemDto create(Long userId, ItemDto itemDto);

	ItemDto update(Long userId, Long itemId, ItemDto itemDto);

	ItemDto getItemById(Long userId, Long itemId);

	Collection<ItemDto> getItemsUser(Long userId, Integer from, Integer size);

	Collection<ItemDto> getItemsByNameOrDescription(Long userId, String text, Integer from, Integer size);

	CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
