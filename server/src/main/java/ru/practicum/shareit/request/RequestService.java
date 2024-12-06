package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.storage.RequestEntity;
import ru.practicum.shareit.user.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;

    public RequestDto createRequest(@Valid RequestDto requestDto, long userId) {
        var userEntity = userStorage.findUserId(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        var requestEntity = RequestMapper.toEntity(requestDto, userEntity);
        requestEntity.setCreated(Timestamp.valueOf(LocalDateTime.now()));

        requestStorage.updateRequest(requestEntity);

        return requestEntity.toDto();
    }

    public List<RequestDto> findRequestsByUserId(long userId, boolean findByEqualUserId) {
        if (findByEqualUserId && !userStorage.existsById(userId))
            throw new NotFoundException("Пользователь не найден");

        var requests = findByEqualUserId
                ? requestStorage.findRequestsByUserId(userId)
                : requestStorage.findRequestsByNotUserId(userId);

        return requests
                .stream()
                .map(RequestEntity::toDto)
                .toList();
    }

    public RequestDto findRequestById(long id) {
        var requestEntity = requestStorage.findRequestById(id)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        return requestEntity.toDto();
    }
}
