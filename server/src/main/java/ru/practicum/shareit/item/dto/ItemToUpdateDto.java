package ru.practicum.shareit.item.dto;

import lombok.Builder;

@Builder
public record ItemToUpdateDto(
    String name,
    String description,
    Boolean available
) { }
