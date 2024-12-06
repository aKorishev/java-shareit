package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToUpdateDto;
import ru.practicum.shareit.request.RequestStorage;
import ru.practicum.shareit.user.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final RequestStorage requestStorage;

    public ItemDto findItem(long itemId) {
        var itemEntity = itemStorage.findItem(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        return itemEntity.toDto();
    }

    public ItemDto createItem(ItemDto item, long userId) {
        var userEntity = userStorage.findUserId(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        var requestEntity = item.requestId() != null
                ? requestStorage.findRequestById(item.requestId())
                .orElseThrow(() -> new NotFoundException("Запрос не найден"))
                : null;

        var itemEntity = ItemMapper.toEntity(item, userEntity, requestEntity);

        itemStorage.updateItem(itemEntity);

        return ItemMapper.toDto(itemEntity);
    }

    public ItemDto updateItem(ItemDto item, long userId) {
        var itemEntityOld = itemStorage.findItem(item.id())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        var owner = itemEntityOld.getOwner();

        if (owner.getId() != userId) {
            throw new NotFoundException("Владелец не найден");
        }

        var requestEntity = item.requestId() != null
                ? requestStorage.findRequestById(item.requestId())
                .orElseThrow(() -> new NotFoundException("Запрос не найден"))
                : null;

        var itemEntity = ItemMapper.toEntity(item, owner, requestEntity);

        itemStorage.updateItem(itemEntity);

        return ItemMapper.toDto(itemEntity);
    }

    public ItemDto updateItem(long itemId, ItemToUpdateDto item, long userId) {
        var itemEntity = itemStorage.findItem(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        var owner = itemEntity.getOwner();

        if (owner.getId() != userId) {
            throw new NotFoundException("Вещь не доступна");
        }

        if (item.name() != null)
            itemEntity.setName(item.name());
        if (item.description() != null)
            itemEntity.setDescription(item.description());
        if (item.available() != null)
            itemEntity.setAvailable(item.available());

        itemStorage.updateItem(itemEntity);

        return ItemMapper.toDto(itemEntity);
    }

    public ItemDto deleteItem(long itemId, long userId) {
        var itemEntityOld = itemStorage.findItem(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        var owner = itemEntityOld.getOwner();

        if (owner.getId() != userId) {
            throw new NotFoundException("Вещь не доступна");
        }

        itemStorage.deleteItem(itemId);

        return ItemMapper.toDto(itemEntityOld);
    }

    public List<ItemDto> getItems(long userId) {
        var userEntity = userStorage.findUserId(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        var itemEntities = userEntity.getItems();

        return itemEntities
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    public List<ItemDto> findFreeItemsByText(String text, boolean available) {
        if (text == null || text.isEmpty())
            return List.of();

        var itemEntities = itemStorage.findItemsByTextAndStatus(text, available);

        return itemEntities
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    public CommentDto addComment(CommentDto commentDto, long itemId, long userId) {
        //log.trace(String.format("addComment: itemId = %d, userId = %d", itemId, userId));

        var userEntity = userStorage.findUserId(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        var itemEntity = itemStorage.findItem(itemId)
                .orElseThrow(() -> new NotFoundException("Вешь не найдена"));

        //log.trace(String.format("addComment: itemEntity = {%s}", itemEntity));

        if (!bookingStorage.existsByBookerIdAndItemIdAndAfterEnd(userId, itemId)) {
            throw new NotValidException("Пользователь не брал вещь, не может оставить комментарий");
        }

        var commentEntity = CommentMapper.toEntity(commentDto, userEntity, itemEntity);

        commentEntity.setCreated(Timestamp.valueOf(LocalDateTime.now()));

        itemStorage.updateComment(commentEntity);

        return CommentMapper.toDto(commentEntity);
    }
}
