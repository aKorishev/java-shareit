package ru.practicum.shareit.user.storage;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.storage.ItemEntity;

import java.util.List;

@Entity
@Table(name = "Users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column
    private String name;
    @Column(nullable = false)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    private List<ItemEntity> items;

    public UserEntity() {

    }
}
