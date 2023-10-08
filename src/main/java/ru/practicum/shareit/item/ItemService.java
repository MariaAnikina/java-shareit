package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UpdateNotYoursItemException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    private ItemStorage itemStorage;
    private UserStorage userStorage;

    public Item create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userStorage.getUsersById(userId));
        validate(item);
        return itemStorage.create(userId, item);
    }

    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        validateUpdate(userId, itemId, itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        validate(itemStorage.getItemById(itemId));
        return itemStorage.update(userId, itemId, item);
    }

    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public Collection<ItemDto> getItemsUser(Long userId) {
        return itemStorage.getItemsUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> getItemsByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getItemsByNameOrDescription(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateUpdate(Long userId, Long itemId, ItemDto item) {
        Item item1 = itemStorage.getItemById(itemId);
        if (!item1.getOwner().getId().equals(userId)) {
            throw new UpdateNotYoursItemException("Нельзя обновлять не свою вещь.");
        }
        if (!Objects.equals(item1.getOwner().getId(), userId)) throw new ValidationException("Изменить можно только параметры своей вещи.");
        if (item.getRequest() != null) {
            throw new ValidationException("Нельзя изменить поле - request.");
        }
    }

    public void validate(Item item) {
        if (item.getName() == null || item.getName().isBlank()) throw new ValidationException("Имя не должно быть пустым.");
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не должно быть пустым.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Информация о бронировании должна быть заполнена.");
        }
        userStorage.getUsersById(item.getOwner().getId());
    }
}
