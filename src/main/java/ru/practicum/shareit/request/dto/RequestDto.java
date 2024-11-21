package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemDto;

import java.sql.Timestamp;
import java.util.List;

@Builder(toBuilder = true)
public record RequestDto(
        Long id,
        String text,
        @NotBlank
        String description,
        Timestamp created,
        List<ItemDto> items
) {
}
