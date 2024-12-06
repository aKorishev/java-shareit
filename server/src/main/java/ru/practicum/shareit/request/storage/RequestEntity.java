package ru.practicum.shareit.request.storage;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.ItemEntity;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "Requests")
@Data
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String text;
    @Column
    private String description;
    @Column
    private Timestamp created;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request")
    private List<ItemEntity> items;

    public RequestEntity() {

    }

    public RequestDto toDto() {
        var items = Optional.ofNullable(getItems())
                .orElse(List.of())
                .stream()
                .map(ItemMapper::toDto)
                .toList();

        return RequestMapper.toDto(this, items);
    }
}
