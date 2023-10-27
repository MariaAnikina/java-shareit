package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    Item getItemById(Long itemId);

    Collection<Item> getItemsUser(Long userId);

    Collection<Item> getItemsByNameOrDescription(String text);
}
