package ru.practicum.shareit.itemrequest.model.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private final Integer id;
    @NotBlank(message = "Описание запроса не может быть пустым")
    private final String description;
    private final LocalDateTime created;
    private final List<ItemDto> items;
}