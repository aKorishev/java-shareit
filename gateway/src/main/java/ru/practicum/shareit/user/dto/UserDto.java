package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
    Long id,

    @NotBlank
    String name,

    @NotNull
    @Email
    String email
) { }
