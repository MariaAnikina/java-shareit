package ru.practicum.shareit.booking.repisitory;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long userId);

	List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

	List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now, LocalDateTime now1);

	List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

	List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, Status waiting);

	List<Booking> findByBookerIdOrderByStartDesc(Long userId);

	List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

	List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now, LocalDateTime now1);

	List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

	List<Booking> findByItemOwnerIdOrderByStartDesc(Long userId);

	List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status waiting);

	Booking  findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(Long id, Status rejected, LocalDateTime now);

	Booking findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(Long id, Status rejected, LocalDateTime now);

	List<Booking> findAllByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime now);
}
