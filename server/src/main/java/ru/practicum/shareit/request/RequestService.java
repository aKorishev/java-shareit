package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.storage.RequestEntity;
import ru.practicum.shareit.user.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;

    public RequestDto createRequest(@Valid RequestDto requestDto, long userId) {
        var userEntity = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        var requestEntity = RequestMapper.toEntity(requestDto, userEntity);
        requestEntity.setCreated(Timestamp.valueOf(LocalDateTime.now()));

        requestStorage.updateRequest(requestEntity);

        return mapToDto(requestEntity);
    }

    public List<RequestDto> getRequestsByUserId(long userId, boolean findByUserId) {
        if (findByUserId && !userStorage.existsById(userId))
            throw new NotFoundException("Пользователь не найден");

        return requestStorage.getRequestsByUserId(userId, findByUserId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public RequestDto getRequestById(long id) {
        var requestEntity = requestStorage.findRequestById(id)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        return mapToDto(requestEntity);
    }

    private RequestDto mapToDto(RequestEntity requestEntity) {
        var items = Optional.ofNullable(requestEntity.getItems())
                .orElse(List.of())
                .stream()
                .map(ItemMapper::toDto)
                .toList();

        return RequestMapper.toDto(requestEntity, items);
    }
}
