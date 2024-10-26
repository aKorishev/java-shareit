package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class ItemToUpdateDto {
    Long itemId;
    @NotNull Optional<String> name;
    @NotNull Optional<String> description;
    @NotNull Optional<Boolean> available;
}
