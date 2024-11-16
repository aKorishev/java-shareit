package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.storage.UserEntity;

public class CommentMapper {
    public CommentDto toDto(
            CommentEntity entity) {
        var build = CommentDto
                .builder()
                .id(entity.getId())
                .text(entity.getText())
                .authorName(entity.getUser().getName())
                .created(entity.getCreated());

        return build.build();
    }

    public CommentEntity toEntity(CommentDto dto, UserEntity userEntity, ItemEntity itemEntity) {
        var entity = new CommentEntity();
        entity.setId(dto.id());
        entity.setText(dto.text());

        entity.setUser(userEntity);
        entity.setItem(itemEntity);

        return entity;
    }
}
