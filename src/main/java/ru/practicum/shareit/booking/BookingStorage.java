package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.storage.BookingEntity;

import java.util.List;
import java.util.Optional;

public interface BookingStorage {
    Optional<BookingEntity> getBooking(long bookingId);

    boolean userIdIsBookerOrOwner(BookingEntity bookingEntity, long userId);

    void updateBooking(BookingEntity entity);

    List<BookingEntity> findBookingsByBookerId(long userId);

    List<BookingEntity> findBookingsByBookerId(long userId, BookingStatus state);

    List<BookingEntity> findBookingsByOwnerId(long ownerId);

    List<BookingEntity> findBookingsByOwnerId(long userId, BookingStatus state);

    boolean existsByBookerIdAndItemIdAndAfterEnd(long userId, long itemId);
}
