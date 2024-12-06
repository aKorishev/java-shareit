package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    List<RequestEntity> findByUserId(long id);

    List<RequestEntity> findByUserIdNot(long id);
}
