package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Qualifier("UserStorageInDB")
@Primary
public class UserStorageInDB implements UserStorage {
    private final UserRepository userRepository;
    private final UserMapper userMapper = new UserMapper();

    @Override
    public Optional<UserEntity> getUser(long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void updateUser(UserEntity user) {
        userRepository.saveAndFlush(user);
    }

    @Override
    public void deleteUser(UserEntity user) {
        userRepository.delete(user);
        userRepository.flush();
    }

    @Override
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }
}
