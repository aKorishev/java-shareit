package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByItemId(long itemId);
}
