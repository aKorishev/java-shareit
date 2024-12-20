package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ItemRequestDto {
    Long id;
    Long userId;
    String text;
}
