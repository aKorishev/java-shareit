package ru.practicum.shareit.booking.storage;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.ItemEntity;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;

@Entity
@Table(name = "Bookings")
@Data
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private UserEntity booker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "start", nullable = false)
    private Timestamp start;

    @Column(name = "finish", nullable = false)
    private Timestamp end;

    public BookingEntity() {

    }

    @Override
    public String toString() {
        return String.format("id=%d,booker=%d,item=%d,status=%s,start=%s,end=%s", id, booker.getId(), item.getId(), status, start.toString(), end.toString());
    }

    public BookingDto toDto() {
        return BookingMapper.toDto(
                this,
                UserMapper.toDto(getBooker()),
                ItemMapper.toDto(getItem()));
    }
}
