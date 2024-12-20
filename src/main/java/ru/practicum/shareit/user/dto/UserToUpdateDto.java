package ru.practicum.shareit.user.dto;

import lombok.Builder;

@Builder
public record UserToUpdateDto(
    Long id,
    String name,
    String email
) { }
