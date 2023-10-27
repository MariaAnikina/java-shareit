package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
	private final ItemService service;

	@PostMapping
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
		return service.create(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
	                      @RequestBody ItemDto itemDto) {
		return service.update(userId, itemId, itemDto);
	}

	@GetMapping("/{itemId}")
	public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
		return service.getItemById(userId, itemId);
	}

	@GetMapping
	public Collection<ItemDto> getItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return service.getItemsUser(userId);
	}

	@GetMapping("/search")
	public Collection<ItemDto> getItemsByNameOrDescription(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                                       @RequestParam String text) {
		return service.getItemsByNameOrDescription(userId, text);
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
		return service.createComment(userId, itemId, commentDto);
	}
}

