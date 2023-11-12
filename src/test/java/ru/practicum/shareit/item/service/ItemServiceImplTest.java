package ru.practicum.shareit.item.service;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repisitory.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Data
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private UserService userService;
	@InjectMocks
	private ItemServiceImpl itemService;

	@Test
	void create_whenItemCreate_thenReturnItem() {
		User user1 = new User(1L, "Ваня", "Van@mail.ru");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				false, new User(1L, "Ваня", "Van@mail.ru"), null);
		when(itemRepository.save(item)).thenReturn(item);
		ItemDto itemDto = ItemMapper.toItemDto(item, null);

		ItemDto itemActualDto = itemService.create(1L, itemDto);
		Optional<User> optionalUser = userRepository.findById(1L);
		User owner = optionalUser.get();
		item.setOwner(owner);

		assertEquals(itemDto, itemActualDto);
	}

	@Test
	void create_whenItemNotValidName_thenReturnUserDoesNotExistException() {
		User user1 = new User(1L, "Ваня", "Van@mail.ru");
		when(userRepository.findById(1L))
				.thenReturn(Optional.of(user1));
		Item item = new Item(1L, null, "Платье для фотоссесии",
				false, new User(1L, "Ваня", "Van@mail.ru"), null);
		Item item2 = new Item(1L, " ", "Платье для фотоссесии",
				false, new User(1L, "Ваня", "Van@mail.ru"), null);
		ItemDto itemDto = ItemMapper.toItemDto(item, null);
		ItemDto itemDto2 = ItemMapper.toItemDto(item2, null);

		assertThrows(ItemNotValidException.class, () ->  itemService.create(user1.getId(), itemDto));
		assertThrows(ItemNotValidException.class, () ->  itemService.create(user1.getId(), itemDto2));
	}

	@Test
	void create_whenItemNotValidDescription_thenReturnUserDoesNotExistException() {
		User user1 = new User(1L, "Ваня", "Van@mail.ru");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
		Item item = new Item(1L, "Платье", null,
				false, new User(1L, "Ваня", "Van@mail.ru"), null);
		Item item2 = new Item(1L, "Платье", "     ",
				false, new User(1L, "Ваня", "Van@mail.ru"), null);
		ItemDto itemDto = ItemMapper.toItemDto(item, null);
		ItemDto itemDto2 = ItemMapper.toItemDto(item2, null);

		assertThrows(ItemNotValidException.class, () ->  itemService.create(user1.getId(), itemDto));
		assertThrows(ItemNotValidException.class, () ->  itemService.create(user1.getId(), itemDto2));
	}

	@Test
	void create_whenItemNotValidAvailable_thenReturnUserDoesNotExistException() {
		User user1 = new User(1L, "Ваня", "Van@mail.ru");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				null, new User(1L, "Ваня", "Van@mail.ru"), null);
		ItemDto itemDto = ItemMapper.toItemDto(item, null);

		assertThrows(ItemNotValidException.class, () ->  itemService.create(user1.getId(), itemDto));
	}

	@Test
	void update_whenItemNotValidName_thenReturnUserDoesNotExistException() {
		User user1 = new User(1L, "Ваня", "Van@mail.ru");
		Item item = new Item(1L, null, "Платье для фотоссесии",
				false, new User(2L, "Ваня", "Van@mail.ru"), null);
		ItemDto itemDto = ItemMapper.toItemDto(item, null);
		when(itemRepository.findById(1L))
				.thenReturn(Optional.of(item));

		assertThrows(ItemOwnerException.class, () ->  itemService.update(user1.getId(), item.getId(), itemDto));
	}

	@Test
	void getItemById_whenItemFound_thenReturnItem() {
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				false, new User(1L, "Ваня", "Van@mail.ru"), null);
		when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(bookingRepository.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
				anyLong(),
				any(Status.class),
				any(LocalDateTime.class)))
				.thenReturn(null);
		when(bookingRepository.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
				anyLong(),
				any(Status.class),
				any(LocalDateTime.class)))
				.thenReturn(null);
		when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(commentRepository.findByItemIdOrderByCreatedDesc(item.getId())).thenReturn(new ArrayList<>());

		ItemDto itemById = itemService.getItemById(1L, 1L);

		assertEquals(ItemMapper.toItemDto(item, new ArrayList<>()), itemById);
	}

	@Test
	void getItemById_whenItemNotFound_thenReturnItemNotFoundException() {
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				false, new User(1L, "Ваня", "Van@mail.ru"), null);
		when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

		assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(1L, 1L));
	}

	@Test
	void getItemsUser_whenItemsFound_thenReturnItems() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				false, user, null);
		Item item2 = new Item(2L, "Сумка", "Сумка для фотоссесии",
				true, user, null);
		Item item3 = new Item(3L, "Стол", "Стол на большую компанию",
				false, new User(2L, "Саша", "Sanya@mail.ru"), null);
		when(userRepository.existsById(1L)).thenReturn(true);
		when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(item, item2));
		when(bookingRepository.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
				anyLong(),
				any(Status.class),
				any(LocalDateTime.class)))
				.thenReturn(null);
		when(bookingRepository.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
				anyLong(),
				any(Status.class),
				any(LocalDateTime.class)))
				.thenReturn(null);
		when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(new ArrayList<>());

		Collection<ItemDto> itemsDto = itemService.getItemsUser(1L, 0,3);

		assertEquals(itemsDto.size(), 2);
		assertTrue(itemsDto.contains(ItemMapper.toItemDto(item, null, null, new ArrayList<>())));
		assertTrue(itemsDto.contains(ItemMapper.toItemDto(item2, null, null, new ArrayList<>())));
	}

	@Test
	void getItemsUser_whenFromNotValid_thenReturnNegativeValueException() {
		assertThrows(NegativeValueException.class, () -> itemService.getItemsUser(1L, -5,3));
	}

	@Test
	void getItemsUser_whenSizeNotValid_thenReturnNegativeValueException() {
		assertThrows(NegativeValueException.class, () -> itemService.getItemsUser(1L, 5,0));
	}

	@Test
	void getItemsByNameOrDescription_whenItemsFound_thenReturnItems() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				false, user, null);
		Item item2 = new Item(2L, "Сумка", "Сумка для фотоссесии",
				true, user, null);
		Item item3 = new Item(3L, "Стол", "Стол на большую компанию",
				false, new User(2L, "Саша", "Sanya@mail.ru"), null);
		List<Item> items = List.of(item, item2);
		when(itemRepository.getItemsByNameOrDescription(anyString())).thenReturn(items);
		when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(new ArrayList<>());

		Collection<ItemDto> itemsDto = itemService.getItemsByNameOrDescription(1L, "для фотоссесии", 0, 3);

		assertEquals(itemsDto.size(), items.size());
		assertTrue(itemsDto.contains(ItemMapper.toItemDto(item, new ArrayList<>())));
		assertTrue(itemsDto.contains(ItemMapper.toItemDto(item2, new ArrayList<>())));
	}

	@Test
	void createComment_whenCommentCreate_thenReturnComment() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				false, user, null);
		LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 6, 5, 4);
		CommentDto commentDto = new CommentDto(1L, "отличное платье", null, null);
		Comment comment = CommentMapper.toComment(commentDto, item, user);
		when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
				.thenReturn(List.of(new Booking()));
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);

		CommentDto result = itemService.createComment(user.getId(), item.getId(),
				CommentMapper.commentToDto(comment, user.getName()));


		verify(itemRepository, times(1)).findById(item.getId());
		verify(userRepository, times(1)).findById(user.getId());
		verify(bookingRepository, times(1)).findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class));
		verify(commentRepository, times(1)).save(any(Comment.class));
		assertThat(result)
				.hasFieldOrPropertyWithValue("id", comment.getId())
				.hasFieldOrPropertyWithValue("text", comment.getText())
				.hasFieldOrPropertyWithValue("authorName", "Ваня")
				.hasFieldOrProperty("created");
	}

	@Test
	void createComment_whenNotBooking_thenReturnBookingTimeException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				false, user, null);
		LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 6, 5, 4);
		CommentDto commentDto = new CommentDto(1L, "отличное платье", null, null);
		Comment comment = CommentMapper.toComment(commentDto, item, user);
		when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
				.thenReturn(new ArrayList<>());

		verify(commentRepository, never()).save(any(Comment.class));
		assertThrows(BookingTimeException.class, () -> itemService.createComment(user.getId(), item.getId(),
				CommentMapper.commentToDto(comment, user.getName())));
	}

	@Test
	void createComment_whenCommentEmpty_thenCommentaryEmptyException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				false, user, null);
		CommentDto commentDto = new CommentDto(1L, "  ", null, null);
		Comment comment = CommentMapper.toComment(commentDto, item, user);

		assertThrows(CommentaryEmptyException.class, () -> itemService.createComment(user.getId(), item.getId(),
				CommentMapper.commentToDto(comment, user.getName())));
	}
}