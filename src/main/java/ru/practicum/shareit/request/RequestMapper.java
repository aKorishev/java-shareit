package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.AnswerDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.storage.RequestEntity;
import ru.practicum.shareit.user.storage.UserEntity;

import java.util.List;

public class RequestMapper {
    public static RequestDto toDto(RequestEntity entity, List<AnswerDto> answers) {
        var build = RequestDto
                .builder()
                .id(entity.getId())
                .text(entity.getText())
                .description(entity.getDescription())
                .date(entity.getDate())
                .answers(answers);

        return build.build();
    }

    public static RequestEntity toEntity(
            RequestDto requestDto,
            UserEntity userEntity) {
        var entity = new RequestEntity();
        entity.setId(requestDto.id());
        entity.setUser(userEntity);
        entity.setText(requestDto.text());
        entity.setDescription(requestDto.description());

        return entity;
    }
}
