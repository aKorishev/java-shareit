package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<ItemDto> getItem(long itemId);

    long addItem(ItemDto item, long userId);

    void updateItem(ItemDto item, long userId);

    Optional<ItemDto> deleteItem(long itemId, long userId);

    List<ItemDto> getItemsForOwner(long userId);

    List<ItemDto> findItemsByTextAndStatus(String text, boolean available);
}
