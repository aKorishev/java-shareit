package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.storage.RequestEntity;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest
public class RequestServiceTest {
    @Mock
    UserStorage userStorage;
    @Mock
    RequestStorage requestStorage;

    @InjectMocks
    RequestService requestService;

    @Test
    public void createRequestWhileNotFoundUserTest() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            requestService.createRequest(RequestDto.builder().build(), 1L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void createRequestTest() {
        var userEntity = new UserEntity();
        userEntity.setId(2L);

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        var requestEntity = new RequestEntity();
        requestEntity.setId(3L);

        var requestDto = RequestDto.builder()
                .id(3L)
                .build();

        var createdDate = Timestamp.valueOf(LocalDateTime.now().minusMinutes(1));

        var actualRequestDto = requestService.createRequest(requestDto, 7L);

        requestEntity.setCreated(createdDate);
        requestEntity.setUser(userEntity);

        var expectedRequestDto = requestEntity.toDto();

        Assertions.assertTrue(createdDate.before(actualRequestDto.created()));

        expectedRequestDto = expectedRequestDto.toBuilder()
                .created(actualRequestDto.created())
                .build();

        Assertions.assertEquals(expectedRequestDto, actualRequestDto);

        Mockito.verify(userStorage, Mockito.times(1))
                .findUserId(7L);

        requestEntity.setCreated(actualRequestDto.created());

        Mockito.verify(requestStorage, Mockito.times(1))
                .updateRequest(requestEntity);
    }

    @Test
    public void findRequestsByUserIdWhileNotFoundUserTest() {
        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(false);

        try {
            requestService.findRequestsByUserId(1L, true);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void findRequestsByUserIdTest() {
        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(true);

        var userEntity = new UserEntity();
        userEntity.setId(1L);

        var requestEntities = Stream.of(
                RequestDto.builder().id(2L).build(),
                RequestDto.builder().id(5L).build(),
                RequestDto.builder().id(7L).build()
        )
                .map(i -> RequestMapper.toEntity(i, userEntity))
                .toList();

        Mockito.when(requestStorage.findRequestsByUserId(Mockito.anyLong()))
                .thenReturn(requestEntities);

        var actualRequestDtos = requestService.findRequestsByUserId(11L, true);

        var expectedRequestDtos = requestEntities.stream().map(RequestEntity::toDto).toList();

        Assertions.assertEquals(expectedRequestDtos, actualRequestDtos);

        Mockito.verify(userStorage, Mockito.times(1))
                .existsById(11L);

        Mockito.verify(requestStorage, Mockito.times(1))
                .findRequestsByUserId(11L);

        Mockito.verify(requestStorage, Mockito.never())
                .findRequestsByNotUserId(Mockito.anyLong());
    }

    @Test
    public void findRequestsByUserIdWhileParamIsFalseTest() {
        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(true);

        var userEntity = new UserEntity();
        userEntity.setId(1L);

        var requestEntities = Stream.of(
                        RequestDto.builder().id(2L).build(),
                        RequestDto.builder().id(5L).build(),
                        RequestDto.builder().id(7L).build()
                )
                .map(i -> RequestMapper.toEntity(i, userEntity))
                .toList();

        Mockito.when(requestStorage.findRequestsByNotUserId(Mockito.anyLong()))
                .thenReturn(requestEntities);

        var actualRequestDtos = requestService.findRequestsByUserId(13L, false);

        var expectedRequestDtos = requestEntities.stream().map(RequestEntity::toDto).toList();

        Assertions.assertEquals(expectedRequestDtos, actualRequestDtos);

        Mockito.verify(userStorage, Mockito.never())
                .existsById(Mockito.anyLong());

        Mockito.verify(requestStorage, Mockito.never())
                .findRequestsByUserId(Mockito.anyLong());

        Mockito.verify(requestStorage, Mockito.times(1))
                .findRequestsByNotUserId(13L);
    }

    @Test
    public void getRequestsIdWhileNotFoundUserTest() {
        Mockito.when(requestStorage.findRequestById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            requestService.findRequestById(1L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Запрос не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Запрос не найден\")");
    }

    @Test
    public void findRequestByIdTest() {
        var requestEntity = new RequestEntity();
        requestEntity.setId(15L);

        Mockito.when(requestStorage.findRequestById(Mockito.anyLong()))
                .thenReturn(Optional.of(requestEntity));

        var actualRequestDto = requestService.findRequestById(11L);

        var expectedRequestDto = requestEntity.toDto();

        Assertions.assertEquals(expectedRequestDto, actualRequestDto);

        Mockito.verify(requestStorage, Mockito.times(1))
                .findRequestById(11L);
    }
}
