package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestExistsException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.NegativeValueException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
	private ItemRequestRepository itemRequestRepository;
	private ItemRepository itemRepository;
	private UserRepository userRepository;

	@Override
	public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
		}
		User user = optionalUser.get();
		if (!itemRepository.getItemsByNameOrDescription(itemRequestDto.getDescription()).isEmpty()) {
			throw new ItemRequestExistsException("Вещь под запрос уже существует");
		}
		ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
		itemRequest.setCreated(LocalDateTime.now());
		itemRequest.setRequestor(user);
		itemRequestRepository.save(itemRequest);
		return ItemRequestMapper.toItemRequestDto(itemRequest);
	}

	@Override
	public List<ItemRequestOutDto> getYourRequests(Long userId) {
		if (userRepository.findById(userId).isEmpty()) {
			throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
		}
		List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorId(userId);
		return itemRequests.stream()
				.map(itemRequest -> ItemRequestMapper.toItemRequestOutDto(itemRequest, new ArrayList<>()))
				.peek(itemRequestOutDto -> {
					List<ItemDto> items = itemRepository.findByRequestId(itemRequestOutDto.getId()).stream()
							.map(item -> ItemMapper.toItemDto(item, null))
							.collect(Collectors.toList());
					itemRequestOutDto.setItems(items);
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<ItemRequestOutDto> getAllRequests(Long userId, Integer from, Integer size) {
		if (from < 0 || size <= 0) {
			throw new NegativeValueException("Значения " + size + " и " + from + " имеют некорректные значения");
		}
		if (userRepository.findById(userId).isEmpty()) {
			throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
		}
		int page = from / size;
		Pageable pageRequest = PageRequest.of(page, size);
		List<ItemRequest> itemRequests = itemRequestRepository
				.findAllByRequestorIdIsNotOrderByCreatedDesc(userId, pageRequest);
		return itemRequests.stream()
				.map(itemRequest -> ItemRequestMapper.toItemRequestOutDto(itemRequest, new ArrayList<>()))
				.peek(itemRequestOutDto -> {
					List<ItemDto> items = itemRepository.findByRequestId(itemRequestOutDto.getId()).stream()
							.map(item -> ItemMapper.toItemDto(item, null))
							.collect(Collectors.toList());
					itemRequestOutDto.setItems(items);
				})
				.collect(Collectors.toList());
	}

	@Override
	public ItemRequestOutDto getByIdRequest(Long userId, Long requestId) {
		if (userRepository.findById(userId).isEmpty()) {
			throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
		}
		Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
		if (itemRequest.isEmpty()) {
			throw new ItemRequestNotFoundException("Запрос не найден");
		}
		List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
				.map(item -> ItemMapper.toItemDto(item, null))
				.collect(Collectors.toList());
		return ItemRequestMapper.toItemRequestOutDto(itemRequest.get(), items);
	}
}
