package ru.practicum.shareit.item.storage;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;

@Entity
@Table(name = "Comments")
@Data
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Timestamp created;

    public CommentEntity() {

    }
}
