package ru.practicum.shareit.user.storage;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserEntity(
    long id,
    String name,
    String email
) { }
