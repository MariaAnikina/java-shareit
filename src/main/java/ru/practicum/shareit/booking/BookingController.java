package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
	private BookingService bookingService;

	@PostMapping
	public BookingDtoFull create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                             @RequestBody @Valid BookingDto bookingDto) {
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
	                                             @RequestParam(defaultValue = "ALL") String state,
	                                             @RequestParam(defaultValue = "0") Integer from,
	                                             @RequestParam(defaultValue = "10")  Integer size) {
		return bookingService.getBooking(userId, state, from, size);
	}

	@GetMapping("/owner")
	public Collection<BookingDtoFull> getYourBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                                 @RequestParam(defaultValue = "ALL") String state,
	                                                 @RequestParam(defaultValue = "0") Integer from,
	                                                 @RequestParam(defaultValue = "10")  Integer size) {
		return bookingService.getYourBooking(userId, state, from, size);
	}
}
