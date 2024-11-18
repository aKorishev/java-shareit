package ru.practicum.shareit.request.storage;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "Request")
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
    private Timestamp date;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request")
    private List<AnswerEntity> answers;

    public RequestEntity() {

    }
}
