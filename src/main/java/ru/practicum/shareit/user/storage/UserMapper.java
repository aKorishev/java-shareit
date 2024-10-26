package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public UserEntity toEntity(UserDto user) {
        return UserEntity
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserDto toApi(UserEntity userEntity) {
        return UserDto
                .builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }
}
