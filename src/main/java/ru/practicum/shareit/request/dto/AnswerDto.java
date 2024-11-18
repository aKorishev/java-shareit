package ru.practicum.shareit.request.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record AnswerDto(
    long itemId,
    String name,
    long userId
) {
};
