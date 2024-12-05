package ru.practicum.shareit.user;

import ru.practicum.shareit.user.storage.UserEntity;

import java.util.Optional;

public interface UserStorage {
    Optional<UserEntity> findUserId(long userId);

    void updateUser(UserEntity user);

    void deleteUser(UserEntity user);

    boolean existsById(long userId);
}
