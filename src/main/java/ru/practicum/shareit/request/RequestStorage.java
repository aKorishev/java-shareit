package ru.practicum.shareit.request;

import ru.practicum.shareit.request.storage.RequestEntity;

import java.util.List;
import java.util.Optional;

public interface RequestStorage {
    void updateRequest(RequestEntity requestEntity);

    List<RequestEntity> getRequestsByUserId(long userId, boolean findByUserId);

    Optional<RequestEntity> findRequestById(long id);
}
