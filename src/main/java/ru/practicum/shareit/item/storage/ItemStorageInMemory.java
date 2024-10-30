package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.*;

@Component
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, ItemEntity> items = new HashMap<>();
    private final Map<Long, Set<ItemEntity>> owners = new HashMap<>();

    private final ItemMapper itemMapper = new ItemMapper();

    @Override
    public Optional<ItemDto> getItem(long itemId) {
        if (!items.containsKey(itemId)) {
            return Optional.empty();
        }

        var itemEntity = items.get(itemId);

        return Optional.of(itemEntity)
                .map(itemMapper::toDto);
    }

    @Override
    public long addItem(ItemDto item, long userId) {
        var itemId = items.isEmpty()
                ? 1
                : items.keySet().stream().max(Long::compareTo).get() + 1;

        var itemEntity = itemMapper.toEntity(itemId, item, userId);

        items.put(itemId, itemEntity);

        if (!owners.containsKey(userId)) {
            var set = new HashSet<ItemEntity>();
            set.add(itemEntity);

            owners.put(userId, set);
        } else {
            var set = owners.get(userId);
            set.add(itemEntity);
        }

        return itemId;
    }

    @Override
    public void updateItem(ItemDto item, long userId) {
        var itemId = item.id();

        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь не найдена");
        }

        var owner = items.get(itemId).owner();

        if (owner != userId) {
            throw new NotValidException("Редактировать вещь может только владелец");
        }

        var itemEntity = itemMapper.toEntity(item, userId);

        items.replace(itemId, itemEntity);
    }

    @Override
    public Optional<ItemDto> deleteItem(long itemId, long userId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь не найдена");
        }

        var oldItem = items.get(itemId);
        var owner = oldItem.owner();

        if (owner != userId) {
            throw new NotValidException("Удалить вещь может только владелец");
        }

        items.remove(itemId);

        return Optional.of(oldItem)
                .map(itemMapper::toDto);
    }

    @Override
    public List<ItemDto> getItemsForOwner(long userId) {
        if (!owners.containsKey(userId)) {
            return List.of();
        }

        return owners.get(userId)
                .stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> findItemsByTextAndStatus(String text, boolean available) {
        var searchText = text.toLowerCase();

        return items.values()
                .stream()
                .filter(i -> i.available() == available
                            && (i.name().toLowerCase().contains(searchText) || i.description().toLowerCase().contains(searchText)))
                .map(itemMapper::toDto)
                .toList();
    }
}
