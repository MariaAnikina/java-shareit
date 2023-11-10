package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {
	ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

	List<ItemRequestOutDto> getYourRequests(Long userId);

	List<ItemRequestOutDto> getAllRequests(Long userId, Integer from, Integer size);

	ItemRequestOutDto getByIdRequest(Long userId, Long requestId);
}
