package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
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
	public List<ItemRequestOutDto> getYourRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemRequestService.getYourRequests(userId);
	}

	@GetMapping("/all")
	public List<ItemRequestOutDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                              @RequestParam(defaultValue = "0") Integer from,
	                                              @RequestParam(defaultValue = "10")  Integer size) {
		return itemRequestService.getAllRequests(userId, from, size);
	}

	@GetMapping("/{requestId}")
	public ItemRequestOutDto getByIdRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                        @PathVariable Long requestId) {
		return itemRequestService.getByIdRequest(userId, requestId);
	}
}
