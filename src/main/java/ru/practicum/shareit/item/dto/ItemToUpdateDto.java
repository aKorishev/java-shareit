package ru.practicum.shareit.item.dto;

import lombok.Builder;

@Builder
public record ItemToUpdateDto(
    Long itemId,
    String name,
    String description,
    Boolean available
) { }
