package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repisitory.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ItemRepository itemRepository;
	@InjectMocks
	private BookingServiceImpl bookingService;

	@Test
	void create_thenBookingCreate_thenReturnBooking() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии",
				true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2023, 12, 6, 12, 12), item.getId(), null, null);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
		when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

		BookingDtoFull result = bookingService.create(user.getId(), bookingDto);

		verify(itemRepository, times(1)).findById(item.getId());
		verify(userRepository, times(1)).findById(user.getId());
		verify(bookingRepository, times(1)).save(any(Booking.class));
		assertThat(result)
				.hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("start", bookingDto.getStart())
				.hasFieldOrPropertyWithValue("end", bookingDto.getEnd())
				.hasFieldOrPropertyWithValue("item", ItemMapper.toItemDto(item, null))
				.hasFieldOrPropertyWithValue("booker", UserMapper.toItemDto(user))
				.hasFieldOrPropertyWithValue("status", Status.WAITING);
	}

	@Test
	void create_thenBookingStartTimeNull_thenReturnBookingTimeException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, null,
				LocalDateTime.of(2023, 12, 6, 12, 12), item.getId(), null, null);

		verify(bookingRepository, never()).save(any(Booking.class));
		assertThrows(BookingTimeException.class, () -> bookingService.create(user.getId(), bookingDto));
	}

	@Test
	void create_thenBookingEndTimeNull_thenReturnBookingTimeException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 6, 12, 12),
				null, item.getId(), null, null);

		verify(bookingRepository, never()).save(any(Booking.class));
		assertThrows(BookingTimeException.class, () -> bookingService.create(user.getId(), bookingDto));
	}

	@Test
	void create_thenUserNotFound_thenReturnUserNotFoundException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2023, 12, 6, 12, 12), item.getId(), null, null);
		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

		verify(bookingRepository, never()).save(any(Booking.class));
		assertThrows(UserNotFoundException.class, () -> bookingService.create(user.getId(), bookingDto));
	}

	@Test
	void create_thenItemNotFound_thenReturnItemNotFoundException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2023, 12, 6, 12, 12), item.getId(), null, null);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.empty());

		verify(bookingRepository, never()).save(any(Booking.class));
		assertThrows(ItemNotFoundException.class, () -> bookingService.create(user.getId(), bookingDto));
	}

	@Test
	void create_thenOwnerItemReservation_thenReturnBookingNotFoundException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, user, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2023, 12, 6, 12, 12), item.getId(), null, null);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

		verify(bookingRepository, never()).save(any(Booking.class));
		assertThrows(BookingNotFoundException.class, () -> bookingService.create(user.getId(), bookingDto));
	}

	@Test
	void create_thenBookingTimeNotValid_thenReturnBookingTimeException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 6, 12, 12),
				LocalDateTime.of(2023, 12, 1, 12, 12), item.getId(), null, null);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

		verify(bookingRepository, never()).save(any(Booking.class));
		assertThrows(BookingTimeException.class, () -> bookingService.create(user.getId(), bookingDto));
	}

	@Test
	void create_thenStartTimeInPast_thenReturnBookingTimeException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2022, 12, 1, 12, 12),
				LocalDateTime.of(2023, 12, 6, 12, 12), item.getId(), null, null);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

		verify(bookingRepository, never()).save(any(Booking.class));
		assertThrows(BookingTimeException.class, () -> bookingService.create(user.getId(), bookingDto));
	}

	@Test
	void create_thenEndTimeInPast_thenReturnBookingTimeException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), null, null);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

		verify(bookingRepository, never()).save(any(Booking.class));
		assertThrows(BookingTimeException.class, () -> bookingService.create(user.getId(), bookingDto));
	}

	@Test
	void updateBookingStatus_whenBookingUpdate_thenReturnBooking() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", null, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.WAITING);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(bookingRepository.findByIdAndItemOwnerId(bookingDto.getId(), user.getId()))
				.thenReturn(Optional.of(booking));
		when(bookingRepository.save(booking)).thenReturn(booking);

		BookingDtoFull result = bookingService.updateBookingStatus(user.getId(), bookingDto.getId(), true);

		verify(bookingRepository, times(1)).findByIdAndItemOwnerId(bookingDto.getId(), user.getId());
		verify(bookingRepository, times(1)).save(any(Booking.class));
		assertThat(result)
				.hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("start", bookingDto.getStart())
				.hasFieldOrPropertyWithValue("end", bookingDto.getEnd())
				.hasFieldOrPropertyWithValue("item", ItemMapper.toItemDto(item, null))
				.hasFieldOrPropertyWithValue("booker", UserMapper.toItemDto(user))
				.hasFieldOrPropertyWithValue("status", Status.APPROVED);
	}

	@Test
	void updateBookingStatus_whenBookingNotFound_thenReturnBookingNotFoundException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", null, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.WAITING);
		when(bookingRepository.findByIdAndItemOwnerId(bookingDto.getId(), user.getId()))
				.thenReturn(Optional.empty());

		assertThrows(BookingNotFoundException.class, () -> bookingService.updateBookingStatus(user.getId(), bookingDto.getId(), false));
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	void updateBookingStatus_whenBookingStatusNotWAITING_thenReturnBookingStatusException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.APPROVED);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(bookingRepository.findByIdAndItemOwnerId(bookingDto.getId(), user.getId()))
				.thenReturn(Optional.of(booking));

		assertThrows(BookingStatusException.class, () -> bookingService.updateBookingStatus(user.getId(), bookingDto.getId(), true));
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	void getBookingInformation_whenBookingInformationFoundUser_thenReturnBooking() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.APPROVED);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(bookingRepository.findById(bookingDto.getId())).thenReturn(Optional.of(booking));

		BookingDtoFull result = bookingService.getBookingInformation(user.getId(), bookingDto.getId());

		verify(bookingRepository, times(1)).findById(anyLong());
		verify(userRepository, times(1)).findById(anyLong());
		assertThat(result)
				.hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("start", bookingDto.getStart())
				.hasFieldOrPropertyWithValue("end", bookingDto.getEnd())
				.hasFieldOrPropertyWithValue("item", ItemMapper.toItemDto(item, null))
				.hasFieldOrPropertyWithValue("booker", UserMapper.toItemDto(user))
				.hasFieldOrPropertyWithValue("status", Status.APPROVED);
	}

	@Test
	void getBookingInformation_whenBookingInformationFoundOvner_thenReturnBooking() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.APPROVED);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
		when(bookingRepository.findById(bookingDto.getId())).thenReturn(Optional.of(booking));

		BookingDtoFull result = bookingService.getBookingInformation(owner.getId(), bookingDto.getId());

		verify(bookingRepository, times(1)).findById(anyLong());
		verify(userRepository, times(1)).findById(anyLong());
		assertThat(result)
				.hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("start", bookingDto.getStart())
				.hasFieldOrPropertyWithValue("end", bookingDto.getEnd())
				.hasFieldOrPropertyWithValue("item", ItemMapper.toItemDto(item, null))
				.hasFieldOrPropertyWithValue("booker", UserMapper.toItemDto(user))
				.hasFieldOrPropertyWithValue("status", Status.APPROVED);
	}

	@Test
	void getBookingInformation_whenBookingInformationFoundNotOwnerAndNotUser_thenReturnBookingNotFoundException() {
		User anotherUser = new User(3L, "Костя", "Kos@mail.ru");
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.APPROVED);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));
		when(bookingRepository.findById(bookingDto.getId())).thenReturn(Optional.of(booking));

		assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingInformation(anotherUser.getId(), bookingDto.getId()));
	}

	@Test
	void getBooking_whenBookingFound_thenReturnBooking() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.APPROVED);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
				any(Pageable.class))).thenReturn(List.of(booking));

		Collection<BookingDtoFull> result = bookingService.getBooking(user.getId(), "FUTURE", 0, 2);

		assertEquals(result.size(), 1);
		assertTrue(result.contains(BookingMapper.toBookingDtoFull(booking)));
	}

	@Test
	void getBooking_whenBookingStateNotValid_thenReturnBookingStateException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.APPROVED);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		assertThrows(BookingStateException.class, () -> bookingService.getBooking(user.getId(), "bad", 0, 2));
	}

	@Test
	void getYourBooking_whenBookingInformationFound_thenReturnBooking() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.APPROVED);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
				any(Pageable.class))).thenReturn(List.of(booking));

		Collection<BookingDtoFull> result = bookingService.getYourBooking(user.getId(), "FUTURE", 0, 2);

		assertEquals(result.size(), 1);
		assertTrue(result.contains(BookingMapper.toBookingDtoFull(booking)));
	}

	@Test
	void getYourBooking_whenBookingInformationNotFound_thenReturnRuntimeException() {
		User user = new User(1L, "Ваня", "Van@mail.ru");
		User owner = new User(2L, "Ваня2", "Van2@mail.ru");
		Item item = new Item(1L, "Платье", "Платье для фотоссесии", true, owner, null);
		BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 1, 12, 12),
				LocalDateTime.of(2022, 12, 6, 12, 12), item.getId(), user.getId(), Status.APPROVED);
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
				any(Pageable.class))).thenReturn(new ArrayList<>());

		assertThrows(RuntimeException.class, () -> bookingService.getYourBooking(user.getId(), "FUTURE", 0, 2));
	}
}