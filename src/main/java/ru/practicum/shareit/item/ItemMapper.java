package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemEntity;
import ru.practicum.shareit.item.storage.ItemWithDateOfBookings;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class ItemMapper {
    public static ItemDto toDto(ItemWithDateOfBookings entity) {
        return toDto(
                entity.getItemEntity(),
                Optional.ofNullable(entity.getLastDateBooking()),
                Optional.ofNullable(entity.getNextDateBooking()),
                List.of());
    }

    public static ItemDto toDto(ItemWithDateOfBookings entity, List<CommentDto> comments) {
        return toDto(
                entity.getItemEntity(),
                Optional.ofNullable(entity.getLastDateBooking()),
                Optional.ofNullable(entity.getNextDateBooking()),
                comments);
    }

    public static ItemDto toDto(ItemEntity itemEntity) {
        return toDto(itemEntity, Optional.empty(),Optional.empty(), List.of());
    }

    public static ItemDto toDto(ItemEntity itemEntity, List<CommentDto> comments) {
        return toDto(itemEntity, Optional.empty(),Optional.empty(), comments);
    }

    public static ItemDto toDto(
            ItemEntity itemEntity,
            @NotNull Optional<Timestamp> lastBooking,
            @NotNull Optional<Timestamp> nextBooking,
            List<CommentDto> comments) {
        var build = ItemDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.isAvailable())
                .comments(comments);

        lastBooking.ifPresent(build::lastBooking);
        nextBooking.ifPresent(build::nextBooking);

        return build.build();
    }

    public static ItemEntity toEntity(ItemDto itemDto, UserEntity userEntity) {
        var entity = new ItemEntity();
        entity.setId(itemDto.id());
        entity.setOwner(userEntity);
        entity.setName(itemDto.name());
        entity.setDescription(itemDto.description());
        entity.setAvailable(itemDto.available());

        return entity;
    }
}
