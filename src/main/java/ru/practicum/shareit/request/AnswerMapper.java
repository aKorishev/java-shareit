package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.AnswerDto;
import ru.practicum.shareit.request.storage.AnswerEntity;

public class AnswerMapper {
    public static AnswerDto toDto(AnswerEntity entity) {
        var itemEntity = entity.getItem();

        var build = AnswerDto
                .builder()
                .itemId(itemEntity.getId())
                .name(itemEntity.getName())
                .userId(itemEntity.getOwner().getId());

        return build.build();
    }
}
