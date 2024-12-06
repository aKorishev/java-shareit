package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemStorage;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Qualifier("ItemStorageInDb")
@Primary
public class ItemStorageInDb implements ItemStorage {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    @Override
    public Optional<ItemEntity> findItem(long itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    public void updateItem(ItemEntity itemEntity) {
        itemRepository.saveAndFlush(itemEntity);
    }

    @Override
    public void updateComment(CommentEntity commentEntity) {
        commentRepository.saveAndFlush(commentEntity);
    }

    @Override
    public void deleteItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<ItemEntity> getItemsForOwner(long userId) {
        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public List<ItemEntity> findItemsByTextAndStatus(String text, boolean available) {
        return itemRepository.findByTextAndAvailable(text, available);
    }

    @Override
    public List<ItemEntity> getAllItems() {
        return itemRepository.findAll();
    }
}
