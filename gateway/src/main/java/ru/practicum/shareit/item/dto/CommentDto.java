package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.sql.Timestamp;

@Builder(toBuilder = true)
public record CommentDto(
    Long id,
    @NotBlank String text,
    String authorName,
    Timestamp created
) { }
