package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public ItemDto toDto(ItemEntity itemEntity) {
        return ItemDto
                .builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.isAvailable())
                .build();
    }

    public ItemEntity toEntity(ItemDto itemDto, long owner) {
        return ItemEntity
                .builder()
                .id(itemDto.getId())
                .owner(owner)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemEntity toEntity(long itemId, ItemDto itemDto, long owner) {
        return ItemEntity
                .builder()
                .id(itemId)
                .owner(owner)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
