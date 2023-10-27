package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Long id;
	@Column(name = "start_time")
	private LocalDateTime start;
	@Column(name = "end_time")
	private LocalDateTime end;
	@JoinColumn(name = "item_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Item item;
	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User booker;
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;
}
