package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.sql.Timestamp;
import java.util.List;

@Builder(toBuilder = true)
public record RequestDto(
        Long id,
        @NotBlank
        String text,
        @NotBlank
        String description,
        Timestamp date,
        List<AnswerDto> answers
) {
}
