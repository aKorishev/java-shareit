package ru.practicum.shareit.request.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.RequestStorage;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Primary
public class RequestStorageInDb implements RequestStorage {
    private final RequestRepository requestRepository;

    @Override
    public void updateRequest(RequestEntity requestEntity) {
        requestRepository.saveAndFlush(requestEntity);
    }

    @Override
    public List<RequestEntity> getRequestsByUserId(long userId, boolean findByUserId) {
        if (findByUserId) {
            return requestRepository.findByUserId(userId);
        } else {
            return requestRepository.findByUserIdNot(userId);
        }
    }

    @Override
    public Optional<RequestEntity> findRequestById(long id) {
        return requestRepository.findById(id);
    }
}
