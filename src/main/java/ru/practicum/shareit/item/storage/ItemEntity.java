package ru.practicum.shareit.item.storage;

import lombok.Builder;

@Builder(toBuilder = true)
public record ItemEntity(
    long id,
    long owner,
    String name,
    String description,
    boolean available
) { }
