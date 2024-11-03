package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public interface UserStorage {
    Optional<UserDto> getUser(long userId);

    long addUser(UserDto user);

    void updateUser(UserDto user);

    Optional<UserDto> deleteUser(long userId);

    boolean containsUser(long userId);
}
