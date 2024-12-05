package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Qualifier("ItemStorageInDb")
@Primary
public class ItemStorageInDb implements ItemStorage {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper = new ItemMapper();
    private final CommentMapper commentMapper = new CommentMapper();

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
    public Optional<ItemEntity> deleteItem(long itemId) {
        var entity = itemRepository.findById(itemId);

        if (entity.isPresent()) {
            itemRepository.deleteById(itemId);
        }

        return entity;
    }

    @Override
    public Optional<CommentEntity> deleteComment(long commentId) {
        var entity = commentRepository.findById(commentId);

        if (entity.isPresent()) {
            commentRepository.deleteById(commentId);
        }

        return entity;
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
