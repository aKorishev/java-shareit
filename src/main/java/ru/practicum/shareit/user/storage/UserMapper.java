package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public UserEntity toEntity(UserDto user) {
        return UserEntity
                .builder()
                .id(user.id())
                .name(user.name())
                .email(user.email())
                .build();
    }

    public UserDto toApi(UserEntity userEntity) {
        return UserDto
                .builder()
                .id(userEntity.id())
                .name(userEntity.name())
                .email(userEntity.email())
                .build();
    }
}
