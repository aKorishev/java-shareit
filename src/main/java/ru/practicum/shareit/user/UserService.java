package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserToUpdateDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto getUser(long userId) {
        var user = userStorage.getUser(userId);

        if (user.isEmpty())
            throw new NotFoundException("Не нашел userId в системе");

        return user.get();
    }

    public long createUser(UserDto user) {
        return userStorage.addUser(user);
    }

    public void updateUser(UserDto user) {
        userStorage.updateUser(user);
    }

    public void updateUser(long userId, UserToUpdateDto user) {
        var oldUser = userStorage.getUser(userId);

        if (oldUser.isEmpty())
            throw new NotFoundException("Не нашел userId в системе");

        var build = oldUser.get().toBuilder();

        user.getName().ifPresent(build::name);
        user.getEmail().ifPresent(build::email);

        updateUser(build.build());
    }

    public UserDto deleteUser(long userId) {
        var user = userStorage.getUser(userId);

        if (user.isEmpty())
            throw new NotFoundException("Не нашел userId в системе");

        userStorage.deleteUser(userId);

        return user.get();
    }
}
