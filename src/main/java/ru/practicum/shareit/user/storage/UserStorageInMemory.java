package ru.practicum.shareit.user.storage;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.IdIsAlreadyInUseException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, UserDto> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();

    @Override
    public Optional<UserDto> getUser(long userId) {
        if (users.containsKey(userId)) {
            return Optional.of(users.get(userId));
        }

        return Optional.empty();
    }

    @Override
    public long addUser(@Valid UserDto user) {
        var email = user.email();

        if (emails.containsKey(email))
            throw new IdIsAlreadyInUseException("Email уже используется");

        var userId = users.isEmpty()
                ? 1L
                : users.keySet().stream().max(Long::compareTo).get() + 1;

        user = user.toBuilder()
                .id(userId)
                .build();

        users.put(userId, user);

        emails.put(email, userId);

        return userId;
    }

    @Override
    public void updateUser(@Valid UserDto user) {
        var userId = user.id();

        if (userId == null)
            throw new NotValidException("Полученный user содержит id = null");

        if (!users.containsKey(userId))
            throw new NotFoundException("Не нашел userId в коллекции");

        var email = user.email();

        if (emails.containsKey(email)) {
            if (!Objects.equals(emails.get(email), userId))
                throw new IdIsAlreadyInUseException("Email уже используется");
        }

        users.replace(userId, user);
    }

    @Override
    public Optional<UserDto> deleteUser(long userId) {
        var oldUser = users.remove(userId);

        users.remove(userId);

        return Optional.ofNullable(oldUser);
    }

    @Override
    public boolean containsUser(long userId) {
        return users.containsKey(userId);
    }
}
