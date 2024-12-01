package ru.practicum.shareit.item.storage;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.request.storage.RequestEntity;
import ru.practicum.shareit.user.storage.UserEntity;

import java.util.List;

@Entity
@Table(name = "Items")
@Data
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false)
    private String name;
    @Column
    private String description;
    @Column
    private boolean available;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    private List<CommentEntity> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private RequestEntity request;

    public ItemEntity() {

    }

    @Override
    public String toString() {
        return String.format("id=%d,owner=%d,name=%s,desc=%s,available=%b", id, owner.getId(), name, description, available);
    }
}
