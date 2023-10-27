package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
	private BookingService bookingService;

	@PostMapping
	public BookingDtoFull create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                             @RequestBody BookingDto bookingDto) {
		return bookingService.create(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public BookingDtoFull updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                          @PathVariable Long bookingId, @RequestParam Boolean approved) {
		return bookingService.updateBookingStatus(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public BookingDtoFull getBookingInformation(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                            @PathVariable Long bookingId) {
		return bookingService.getBookingInformation(userId, bookingId);
	}

	@GetMapping
	public Collection<BookingDtoFull> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                             @RequestParam(defaultValue = "ALL") String state) {
		return bookingService.getBooking(userId, state);
	}

	@GetMapping("/owner")
	public Collection<BookingDtoFull> getYourBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                                 @RequestParam(defaultValue = "ALL") String state) {
		return bookingService.getYourBooking(userId, state);
	}
}
