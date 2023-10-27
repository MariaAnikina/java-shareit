package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
	private BookingRepository bookingRepository;
	private UserRepository userRepository;
	private ItemRepository itemRepository;

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
		if (!bookingDto.getStart().isAfter(LocalDateTime.now()) || !bookingDto.getEnd().isAfter(LocalDateTime.now()) ) {
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
		if (!userRepository.existsById(userId))
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
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
	public Collection<BookingDtoFull> getBooking(Long userId, String state) {
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
				bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
				break;
			case CURRENT:
				LocalDateTime now = LocalDateTime.now();
				bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
				break;
			case WAITING:
				bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
				break;
			case PAST:
				bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
				break;
			case REJECTED:
				bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
				break;
			default:
				bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
		}
		return bookings.stream().map(BookingMapper::toBookingDtoFull).collect(Collectors.toList());
	}

	@Override
	public Collection<BookingDtoFull> getYourBooking(Long userId, String state) {
		BookingState bookingState;
		try {
			bookingState = valueOf(state);
		} catch (IllegalArgumentException e) {
			throw new BookingStateException("Unknown state: "  + state);
		}
		List<Booking> bookings;
		switch (bookingState) {
			case FUTURE:
				bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
				break;
			case CURRENT:
				LocalDateTime now = LocalDateTime.now();
				bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
				break;
			case PAST:
				bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
				break;
			case WAITING:
				bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
				break;
			case REJECTED:
				bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
				break;
			default:
				bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
		}
		if (bookings.size() == 0) {
			throw new RuntimeException("Бронирований нет");
		}
		return bookings.stream().map(BookingMapper::toBookingDtoFull).collect(Collectors.toList());
	}
}
