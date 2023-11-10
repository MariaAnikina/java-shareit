package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repisitory.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingState.valueOf;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	public BookingDtoFull create(Long userId, BookingDto bookingDto) {
		if (bookingDto.getEnd() == null || bookingDto.getStart() == null) {
			throw new BookingTimeException("Время начала и окончания бронирования не должны быть пустыми");
		}
		bookingDto.setBookerId(userId);
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id="
				+ userId + " не найден");
		User user = userOptional.get();
		Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());
		if (itemOptional.isEmpty()) throw new ItemNotFoundException("Вещь с id="
				+ bookingDto.getItemId() + " не найдена");
		Item item = itemOptional.get();
		if (Objects.equals(item.getOwner().getId(), userId))
			throw new BookingNotFoundException("Пользователь не может забронировать свою вещь");
		if (!item.getAvailable()) throw new ItemUnavailableException("Вещь с id=" + item.getId() + " не доступна");
		if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
			throw new BookingTimeException("Момент окончания бронирования должен быть позже начала");
		}
		if (!bookingDto.getStart().isAfter(LocalDateTime.now()) || !bookingDto.getEnd().isAfter(LocalDateTime.now())) {
			throw new BookingTimeException("Момент окончания и начала бронирования должен быть в будущем");
		}
		Booking booking = BookingMapper.toBooking(bookingDto, user, item);
		booking.setStatus(Status.WAITING);
		bookingRepository.save(booking);
		return BookingMapper.toBookingDtoFull(booking);
	}

	@Override
	public BookingDtoFull updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
		Optional<Booking> bookingOptional = bookingRepository.findByIdAndItemOwnerId(bookingId, userId);
		if (bookingOptional.isEmpty())
			throw new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено");
		Booking booking = bookingOptional.get();
		if (!booking.getStatus().equals(Status.WAITING))
			throw new BookingStatusException("Статус бронирования не является 'WAITING'");
		if (approved) {
			booking.setStatus(Status.APPROVED);
		} else {
			booking.setStatus(Status.REJECTED);
		}
		bookingRepository.save(booking);
		return BookingMapper.toBookingDtoFull(booking);
	}

	@Override
	public BookingDtoFull getBookingInformation(Long userId, Long bookingId) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id="
				+ userId + " не найден");
		Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
		if (bookingOptional.isEmpty()) {
			throw new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено");
		}
		Booking booking = bookingOptional.get();
		if (!Objects.equals(booking.getItem().getOwner().getId(), userId)
				&& !Objects.equals(booking.getBooker().getId(), userId))
			throw new BookingNotFoundException(
					"У пользователя с id=" + userId + " не обнаружено бронирований с id=" + bookingId
			);
		return BookingMapper.toBookingDtoFull(booking);
	}

	@Override
	public Collection<BookingDtoFull> getBooking(Long userId, String state, Integer from, Integer size) {
		if (from < 0 || size <= 0) {
			throw new NegativeValueException("Значения " + size + " и " + from + " имеют некорректные значения");
		}
		int page = from / size;
		Pageable pageRequest = PageRequest.of(page, size);
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) throw new UserNotFoundException("Пользователь с id="
				+ userId + " не найден");
		BookingState bookingState;
		try {
			bookingState = BookingState.valueOf(state);
		} catch (IllegalArgumentException e) {
			throw new BookingStateException("Unknown state: " + state);
		}
		List<Booking> bookings;
		switch (bookingState) {
			case FUTURE:
				bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(),
						pageRequest);
				break;
			case CURRENT:
				LocalDateTime now = LocalDateTime.now();
				bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now,
						pageRequest);
				break;
			case WAITING:
				bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
						pageRequest);
				break;
			case PAST:
				bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(),
						pageRequest);
				break;
			case REJECTED:
				bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
						pageRequest);
				break;
			default:
				bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageRequest);
		}
		return bookings.stream().map(BookingMapper::toBookingDtoFull).collect(Collectors.toList());
	}

	@Override
	public Collection<BookingDtoFull> getYourBooking(Long userId, String state, Integer from, Integer size) {
		if (from < 0 || size <= 0) {
			throw new NegativeValueException("Значения " + size + " и " + from + " имеют некорректные значения");
		}
		int page = from / size;
		Pageable pageRequest = PageRequest.of(page, size);
		BookingState bookingState;
		try {
			bookingState = valueOf(state);
		} catch (IllegalArgumentException e) {
			throw new BookingStateException("Unknown state: " + state);
		}
		List<Booking> bookings;
		switch (bookingState) {
			case FUTURE:
				bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(),
						pageRequest);
				break;
			case CURRENT:
				LocalDateTime now = LocalDateTime.now();
				bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now,
						pageRequest);
				break;
			case PAST:
				bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(),
						pageRequest);
				break;
			case WAITING:
				bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
						pageRequest);
				break;
			case REJECTED:
				bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
						pageRequest);
				break;
			default:
				bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pageRequest);
		}
		if (bookings.size() == 0) {
			throw new RuntimeException("Бронирований нет");
		}
		return bookings.stream().map(BookingMapper::toBookingDtoFull).collect(Collectors.toList());
	}
}
