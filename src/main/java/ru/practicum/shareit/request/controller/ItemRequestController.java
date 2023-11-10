package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
	private ItemRequestService itemRequestService;

	@PostMapping
	public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
		return itemRequestService.create(userId, itemRequestDto);
	}

	@GetMapping
	public List<ItemRequestDto> getYourRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemRequestService.getYourRequests(userId);
	}

	@GetMapping("/all")
	public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                           @RequestParam Integer from, @RequestParam Integer size) {
		return itemRequestService.getAllRequests(userId, size);
	}

	@GetMapping("/{requestId}")
	public List<ItemRequestDto> getByIdRequest(@PathVariable Long requestId) {
		return itemRequestService.getByIdRequest(requestId);
	}
}
