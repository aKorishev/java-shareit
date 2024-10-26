package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class UserToUpdateDto {
    Long id;

    @NotNull
    Optional<String> name;

    @NotNull
    Optional<String> email;
}
