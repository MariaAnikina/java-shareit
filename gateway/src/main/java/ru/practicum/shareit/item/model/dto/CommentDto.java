package ru.practicum.shareit.item.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private final Integer id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}
