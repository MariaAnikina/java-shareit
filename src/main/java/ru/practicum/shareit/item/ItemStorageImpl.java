package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private Map<Long, Item> items = new HashMap<>();
    private Long lastId = 0L;

    @Override
    public Item create(Long userId, Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        Item item1 = items.get(itemId);
        if (item.getName() != null) {
            item1.setName(item.getName());
        }
        if (item.getDescription() != null) {
            item1.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            item1.setAvailable(item.getAvailable());
        }
        return items.put(itemId, item1);
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getItemsUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemsByNameOrDescription(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private Long getId() {
        return ++lastId;
    }
}
