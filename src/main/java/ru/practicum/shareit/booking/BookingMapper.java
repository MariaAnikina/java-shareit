package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class BookingMapper {
	public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {
		return new Booking(
				bookingDto.getId(),
				bookingDto.getStart(),
				bookingDto.getEnd(),
				item,
				booker,
				bookingDto.getStatus()
		);
	}

	public static BookingDtoFull toBookingDtoFull(Booking booking) {
		return new BookingDtoFull(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				ItemMapper.toItemDto(booking.getItem(), null, null, null),
				UserMapper.toItemDto(booking.getBooker()),
				booking.getStatus()
		);
	}

	public static BookingDto toBookingDto(Booking booking) {
		return new BookingDto(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				booking.getItem().getId(),
				booking.getBooker().getId(),
				booking.getStatus()
		);
	}
}
