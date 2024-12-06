package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Qualifier("ItemStorageInDb")
@Primary
public class BookingStorageInDb implements BookingStorage {
    private final BookingRepository bookingRepository;

    @Override
    public Optional<BookingEntity> findBooking(long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public void updateBooking(BookingEntity bookingEntity) {
        bookingRepository.saveAndFlush(bookingEntity);
    }

    @Override
    public List<BookingEntity> findBookingsByBookerId(long userId) {
        return bookingRepository.findByBookerId(userId);
    }

    @Override
    public List<BookingEntity> findBookingsByBookerId(long userId, BookingStatus state) {
        return bookingRepository.findByBookerIdAndStatus(userId, state);
    }

    @Override
    public List<BookingEntity> findBookingsByOwnerId(long userId) {
        return bookingRepository.findByItemOwner(userId);
    }

    @Override
    public List<BookingEntity> findBookingsByOwnerId(long userId, BookingStatus state) {
        return bookingRepository.findByItemOwnerAndStatus(userId, state);
    }

    @Override
    public boolean existsByBookerIdAndItemIdAndAfterEnd(long userId, long itemId) {
        var date = Timestamp.from(Instant.now());
        return bookingRepository.existsByBookerIdAndItemIdAndEndLessThan(userId, itemId, date);
    }
}
