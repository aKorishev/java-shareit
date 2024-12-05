package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserToUpdateDto;
import ru.practicum.shareit.user.storage.UserEntity;

import java.util.Optional;

@SpringBootTest
public class UserServiceTest {
    @Mock
    UserStorage userStorage;

    @InjectMocks
    UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void findUserIdWhileNotFoundUserTest() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            userService.findUserId(10L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Не нашел userId в системе", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Не нашел userId в системе\")");
    }

    @Test
    public void findUserIdTest() {
        var userEntity = new UserEntity();
        userEntity.setId(2L);

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        var expectedUserDto = UserMapper.toDto(userEntity);

        var actualUserDto = userService.findUserId(5L);

        Assertions.assertEquals(expectedUserDto, actualUserDto);

        Mockito.verify(userStorage, Mockito.times(1))
                .findUserId(5L);
    }

    @Test
    public void updateUserTest() {
        var userDto = UserDto.builder()
                .id(10L)
                .name("test")
                .build();

        var actualUserDto = userService.updateUser(userDto);

        Assertions.assertEquals(userDto, actualUserDto);

        Mockito.verify(userStorage, Mockito.times(1))
                .updateUser(UserMapper.toEntity(userDto));
    }

    @Test
    public void updateUserByRequestWhileNotFoundUserId() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        var userRequestToUpdate = UserToUpdateDto.builder().build();

        try {
            userService.updateUser(10L, userRequestToUpdate);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Не нашел userId в системе", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Не нашел userId в системе\")");
    }

    @Test
    public void updateUserByRequestChangeName() {
        var userDto = UserDto.builder()
                .id(10L)
                .name("test")
                .build();

        var updateDto = UserToUpdateDto.builder()
                .name("updated")
                .build();

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.toEntity(userDto)));

        var userEntity = UserMapper.toEntity(userDto);
        userEntity.setName("updated");

        var actualUserDto = userService.updateUser(1L, updateDto);

        Assertions.assertEquals("updated", actualUserDto.name());

        Mockito.verify(userStorage, Mockito.times(1))
                .findUserId(1L);

        Mockito.verify(userStorage, Mockito.times(1))
                .updateUser(userEntity);
    }

    @Test
    public void updateUserByRequestChangeEmail() {
        var userDto = UserDto.builder()
                .id(10L)
                .email("test@aa.bb")
                .build();

        var updateDto = UserToUpdateDto.builder()
                .email("updated@aa.bb")
                .build();

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.toEntity(userDto)));

        var userEntity = UserMapper.toEntity(userDto);
        userEntity.setEmail("updated@aa.bb");

        var actualUserDto = userService.updateUser(1L, updateDto);

        Assertions.assertEquals("updated@aa.bb", actualUserDto.email());

        Mockito.verify(userStorage, Mockito.times(1))
                .findUserId(1L);

        Mockito.verify(userStorage, Mockito.times(1))
                .updateUser(userEntity);
    }

    @Test
    public void updateUserByRequestNotChanges() {
        var userDto = UserDto.builder()
                .id(10L)
                .name("test")
                .email("test@aa.bb")
                .build();

        var updateDto = UserToUpdateDto.builder()
                .build();

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.toEntity(userDto)));

        var userEntity = UserMapper.toEntity(userDto);

        var actualUserDto = userService.updateUser(1L, updateDto);

        Assertions.assertEquals(userDto, actualUserDto);

        Mockito.verify(userStorage, Mockito.times(1))
                .findUserId(1L);

        Mockito.verify(userStorage, Mockito.times(1))
                .updateUser(userEntity);
    }

    @Test
    public void deleteUserByRequestWhileNotFoundUserId() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            userService.deleteUser(10L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Не нашел userId в системе", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Не нашел userId в системе\")");
    }

    @Test
    public void deleteUserTest() {
        var oldUserEntity = new UserEntity();
        oldUserEntity.setId(17L);

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(oldUserEntity));

        var expectedUserDto = UserMapper.toDto(oldUserEntity);

        var actualUserDto = userService.deleteUser(5L);

        Assertions.assertEquals(expectedUserDto, actualUserDto);

        Mockito.verify(userStorage, Mockito.times(1))
                        .findUserId(5L);

        Mockito.verify(userStorage, Mockito.times(1))
                .deleteUser(oldUserEntity);
    }
}
