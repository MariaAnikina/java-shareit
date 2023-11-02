package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long id;
	@Column(length = 1000)
	private String text;
	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;
	private LocalDateTime created;
}
