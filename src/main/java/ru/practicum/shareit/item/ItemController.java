package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return service.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return service.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItemsUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsByNameOrDescription(@RequestParam String text) {
        return  service.getItemsByNameOrDescription(text);
    }
}

