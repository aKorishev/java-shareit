package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserToUpdateDto;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper = new UserMapper();

    public UserDto getUser(long userId) {
        var userEntity = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Не нашел userId в системе"));

        return userMapper.toDto(userEntity);
    }

    public UserDto updateUser(UserDto user) {
        var userEntity = userMapper.toEntity(user);

        userStorage.updateUser(userEntity);

        return userMapper.toDto(userEntity);
    }

    public UserDto updateUser(long userId, UserToUpdateDto user) {
        var userEntity = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Не нашел userId в системе"));

        if (user.name() != null)
            userEntity.setName(user.name());
        if (user.email() != null)
            userEntity.setEmail(user.email());

        userStorage.updateUser(userEntity);

        return userMapper.toDto(userEntity);
    }

    public UserDto deleteUser(long userId) {
        var userEntity = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Не нашел userId в системе"));

        userStorage.deleteUser(userEntity);

        return userMapper.toDto(userEntity);
    }
}
