package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class CommentDto {
	private Long id;
	@NotBlank(message = "Текст комментария не может быть пустым")
	private String text;
	private String authorName;
	private LocalDateTime created;
}