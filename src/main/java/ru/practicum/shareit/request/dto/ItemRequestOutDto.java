package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;


import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class ItemRequestOutDto {
	private Long id;
	private String description;
	private LocalDateTime created;
	private List<ItemDto> items;
}
