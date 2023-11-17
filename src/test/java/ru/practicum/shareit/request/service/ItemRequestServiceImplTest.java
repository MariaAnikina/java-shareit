package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ItemRequestExistsException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.NegativeValueException;
import ru.practicum.shareit.item.model.Item;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
	@Mock
	private ItemRequestRepository itemRequestRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private ItemRequestServiceImpl itemRequestService;

	@Test
	void create_whenRequestCreate_thenReturnRequest() {
		ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Удочка", null);
		User user = new User(1L, "Тарас", "tar@mail.ru");
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRepository.getItemsByNameOrDescription(itemRequestDto.getDescription())).thenReturn(List.of());
		when(itemRequestRepository.save(any(ItemRequest.class)))
				.thenReturn(ItemRequestMapper.toItemRequest(itemRequestDto, user));

		ItemRequestDto result = itemRequestService.create(user.getId(), itemRequestDto);

		verify(userRepository, times(1)).findById(user.getId());
		verify(itemRepository, times(1)).getItemsByNameOrDescription(itemRequestDto.getDescription());
		verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
		assertThat(result)
				.hasFieldOrPropertyWithValue("id", itemRequestDto.getId())
				.hasFieldOrPropertyWithValue("description", itemRequestDto.getDescription())
				.hasFieldOrProperty("created");
	}

	@Test
	void create_whenIteAlreadyExists_thenReturnItemRequestExistsException() {
		ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Удочка", null);
		User user = new User(1L, "Тарас", "tar@mail.ru");
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRepository.getItemsByNameOrDescription(itemRequestDto.getDescription())).thenReturn(List.of(new Item()));

		verify(itemRequestRepository, never()).save(any(ItemRequest.class));
		assertThrows(ItemRequestExistsException.class, () -> itemRequestService.create(user.getId(), itemRequestDto));
	}

	@Test
	void getYourRequests_whenRequestsFound_thenReturnRequests() {
		ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Удочка", null);
		ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "Лодка", null);
		Item item = new Item();
		User user = new User(1L, "Тарас", "tar@mail.ru");
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRequestRepository.findByRequestorId(user.getId()))
				.thenReturn(List.of(
						ItemRequestMapper.toItemRequest(itemRequestDto2, user),
						ItemRequestMapper.toItemRequest(itemRequestDto, user))
				);
		when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
		when(itemRepository.findByRequestId(2L)).thenReturn(new ArrayList<>());

		List<ItemRequestOutDto> result = itemRequestService.getYourRequests(user.getId());

		verify(userRepository, times(1)).findById(user.getId());
		verify(itemRepository, times(2)).findByRequestId(anyLong());
		verify(itemRequestRepository, times(1)).findByRequestorId(user.getId());
		assertEquals(result.size(), 2);
		assertThat(result.get(0))
				.hasFieldOrPropertyWithValue("id", itemRequestDto2.getId())
				.hasFieldOrPropertyWithValue("description", itemRequestDto2.getDescription())
				.hasFieldOrProperty("created")
				.hasFieldOrPropertyWithValue("items", new ArrayList<>());
		assertEquals(result.get(1).getItems().size(), 1);
	}

	@Test
	void getAllRequests_whenAllRequestsFound_thenReturnAllRequests() {
		ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Удочка", LocalDateTime.now());
		ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "Лодка", LocalDateTime.now());
		ItemRequestDto itemRequestDto3 = new ItemRequestDto(3L, "Туфли", LocalDateTime.now());
		User user = new User(1L, "Тарас", "tar@mail.ru");
		User user2 = new User(2L, "Галя", "galya@mail.ru");
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRequestRepository
				.findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any(Pageable.class)))
				.thenReturn(List.of(ItemRequestMapper.toItemRequest(itemRequestDto3, user2))
				);
		when(itemRepository.findByRequestId(anyLong())).thenReturn(new ArrayList<>());

		List<ItemRequestOutDto> result = itemRequestService.getAllRequests(user.getId(), 0, 3);

		verify(userRepository, times(1)).findById(user.getId());
		verify(itemRepository, times(1)).findByRequestId(anyLong());
		verify(itemRequestRepository, times(1))
				.findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any(Pageable.class));
		assertEquals(result.size(), 1);
		assertThat(result.get(0))
				.hasFieldOrPropertyWithValue("id", itemRequestDto3.getId())
				.hasFieldOrPropertyWithValue("description", itemRequestDto3.getDescription())
				.hasFieldOrProperty("created")
				.hasFieldOrPropertyWithValue("items", new ArrayList<>());
	}

	@Test
	void getAllRequests_whenFromNotValid_thenReturnNegativeValueException() {
		assertThrows(NegativeValueException.class, () -> itemRequestService.getAllRequests(1L, -5, 3));
	}

	@Test
	void getAllRequests_whenSizeNotValid_thenReturnNegativeValueException() {
		assertThrows(NegativeValueException.class, () -> itemRequestService.getAllRequests(1L, 5, 0));
	}

	@Test
	void getByIdRequest_thenRequestFound_thenReturnRequest() {
		ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Удочка", LocalDateTime.now());
		User user = new User(1L, "Тарас", "tar@mail.ru");
		User user2 = new User(2L, "Олег", "oleg@mail.ru");
		when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
		when(itemRequestRepository.findById(itemRequestDto.getId()))
				.thenReturn(Optional.of(ItemRequestMapper.toItemRequest(itemRequestDto, user)));
		when(itemRepository.findByRequestId(anyLong())).thenReturn(new ArrayList<>());

		ItemRequestOutDto result = itemRequestService.getByIdRequest(user2.getId(), itemRequestDto.getId());

		verify(userRepository, times(1)).findById(anyLong());
		verify(itemRepository, times(1)).findByRequestId(anyLong());
		verify(itemRequestRepository, times(1)).findById(anyLong());
		assertThat(result)
				.hasFieldOrPropertyWithValue("id", itemRequestDto.getId())
				.hasFieldOrPropertyWithValue("description", itemRequestDto.getDescription())
				.hasFieldOrProperty("created")
				.hasFieldOrPropertyWithValue("items", new ArrayList<>());
	}

	@Test
	void getByIdRequest_thenRequestNotFound_thenReturnItemRequestNotFoundException() {
		ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Удочка", LocalDateTime.now());
		User user = new User(1L, "Тарас", "tar@mail.ru");
		User user2 = new User(2L, "Олег", "oleg@mail.ru");
		when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
		when(itemRequestRepository.findById(itemRequestDto.getId()))
				.thenReturn(Optional.empty());

		assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getByIdRequest(user2.getId(), itemRequestDto.getId()));
		verify(userRepository, times(1)).findById(anyLong());
		verify(itemRepository, never()).findByRequestId(anyLong());
		verify(itemRequestRepository, times(1)).findById(anyLong());
	}
}