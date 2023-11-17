package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;

import java.util.Collection;

public interface BookingService {
	BookingDtoFull create(Long userId, BookingDto bookingDto);

	BookingDtoFull updateBookingStatus(Long userId, Long bookingId, Boolean approved);

	BookingDtoFull getBookingInformation(Long userId, Long bookingId);

	Collection<BookingDtoFull> getBooking(Long userId, String status, Integer from, Integer size);

	Collection<BookingDtoFull> getYourBooking(Long userId, String state, Integer from, Integer size);
}
