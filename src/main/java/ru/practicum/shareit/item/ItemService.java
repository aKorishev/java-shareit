package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToUpdateDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto getItem(long itemId) {
        var item = itemStorage.getItem(itemId);

        if (item.isEmpty())
            throw new NotFoundException("Вещь не найдена");

        return item.get();
    }

    public long createItem(ItemDto item, long userId) {
        checkContainsUserId(userId);

        return itemStorage.addItem(item, userId);
    }

    public ItemDto updateItem(ItemDto item, long userId) {
        checkContainsUserId(userId);

        itemStorage.updateItem(item, userId);

        return itemStorage.getItem(item.getId()).get();
    }

    public ItemDto updateItem(long itemId, ItemToUpdateDto item, long userId) {
        var oldItem = itemStorage.getItem(itemId);

        if (oldItem.isEmpty())
            throw new NotFoundException("Не нашел предмет в системе");

        checkContainsUserId(userId);

        var build = oldItem.get().toBuilder();

        item.getName().ifPresent(build::name);
        item.getDescription().ifPresent(build::description);
        item.getAvailable().ifPresent(build::available);

        itemStorage.updateItem(build.build(), userId);

        return itemStorage.getItem(itemId).get();
    }

    public ItemDto deleteItem(long itemId, long userId) {
        var oldItem = itemStorage.deleteItem(itemId, userId);

        if (oldItem.isEmpty())
            throw new NotFoundException("Вещь не найдена");

        return oldItem.get();
    }

    public List<ItemDto> getItems(long userId) {
        checkContainsUserId(userId);

        return itemStorage.getItemsForOwner(userId);
    }

    public List<ItemDto> findFreeItemsByText(String text) {
        if (text == null || text.isEmpty())
            return List.of();

        return itemStorage.findItemsByTextAndStatus(text, true);
    }

    private void checkContainsUserId(long userId) {
        if (!userStorage.containsUser(userId))
            throw new NotFoundException("Пользователь не найден");
    }
}
