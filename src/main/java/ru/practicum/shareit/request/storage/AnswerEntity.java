package ru.practicum.shareit.request.storage;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.storage.ItemEntity;

@Entity
@Table(name = "Answers")
@Data
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private RequestEntity request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    public AnswerEntity() {

    }
}
