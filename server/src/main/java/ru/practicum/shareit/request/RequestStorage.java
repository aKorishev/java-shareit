package ru.practicum.shareit.request;

import ru.practicum.shareit.request.storage.RequestEntity;

import java.util.List;
import java.util.Optional;

public interface RequestStorage {
    void updateRequest(RequestEntity requestEntity);

    List<RequestEntity> findRequestsByUserId(long userId);

    List<RequestEntity> findRequestsByNotUserId(long userId);

    Optional<RequestEntity> findRequestById(long id);
}
