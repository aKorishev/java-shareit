package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public ItemDto toDto(ItemEntity itemEntity) {
        return ItemDto
                .builder()
                .id(itemEntity.id())
                .name(itemEntity.name())
                .description(itemEntity.description())
                .available(itemEntity.available())
                .build();
    }

    public ItemEntity toEntity(ItemDto itemDto, long owner) {
        return ItemEntity
                .builder()
                .id(itemDto.id())
                .owner(owner)
                .name(itemDto.name())
                .description(itemDto.description())
                .available(itemDto.available())
                .build();
    }

    public ItemEntity toEntity(long itemId, ItemDto itemDto, long owner) {
        return ItemEntity
                .builder()
                .id(itemId)
                .owner(owner)
                .name(itemDto.name())
                .description(itemDto.description())
                .available(itemDto.available())
                .build();
    }
}
