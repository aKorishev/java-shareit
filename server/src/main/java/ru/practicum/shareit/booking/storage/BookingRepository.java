package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.BookingStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByBookerId(long userId);

    List<BookingEntity> findByBookerIdAndStatus(long userId, BookingStatus status);

    boolean existsByBookerId(long userId);

    @Query("select b " +
            "from BookingEntity b " +
            "   join b.item i " +
            "where i.owner.id = :ownerId")
    List<BookingEntity> findByItemOwner(@Param("ownerId") long ownerId);

    @Query("select b " +
            "from BookingEntity b " +
            "  join b.item i " +
            "where i.owner.id = :ownerId and b.status = :status")
    List<BookingEntity> findByItemOwnerAndStatus(
            @Param("ownerId") long ownerId,
            @Param("status") BookingStatus status);

    boolean existsByBookerIdAndItemIdAndEndLessThan(long userId, long itemId, Timestamp date);

    @Query("Select min(b.start) " +
            "from BookingEntity b " +
            "where b.item.id = :itemId and b.start > :date")
    Optional<Timestamp> findDateNextBooking(
            @Param("itemId") long itemId,
            @Param("date") Timestamp date);

    @Query("Select max(b.end) " +
            "from BookingEntity b " +
            "where b.item.id = :itemId and b.end < :date")
    Optional<Timestamp> findDateLastBooking(
            @Param("itemId") long itemId,
            @Param("date") Timestamp date);
}
