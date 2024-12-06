package ru.practicum.shareit.item;

import ru.practicum.shareit.item.storage.CommentEntity;
import ru.practicum.shareit.item.storage.ItemEntity;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<ItemEntity> findItem(long itemId);

    void updateItem(ItemEntity itemEntity);

    void updateComment(CommentEntity commentEntity);

    void deleteItem(long itemId);

    void deleteComment(long commentId);

    List<ItemEntity> getItemsForOwner(long userId);

    List<ItemEntity> findItemsByTextAndStatus(String text, boolean available);

    List<ItemEntity> getAllItems();
}
