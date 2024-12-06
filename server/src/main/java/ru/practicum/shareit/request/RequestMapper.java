package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.storage.RequestEntity;
import ru.practicum.shareit.user.storage.UserEntity;

import java.util.List;

public class RequestMapper {
    public static RequestDto toDto(RequestEntity entity, List<ItemDto> items) {
        var build = RequestDto
                .builder()
                .id(entity.getId())
                .text(entity.getText())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .items(items);

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

        var created = requestDto.created();

        if (created != null)
            entity.setCreated(created);

        return entity;
    }
}
