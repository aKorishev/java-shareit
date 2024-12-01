package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    @Query("select i " +
            "from ItemEntity i " +
            "where (   lower(i.name) like lower(concat('%', :text,'%')) " +
            "       or lower(i.description) like lower(concat('%', :text,'%'))) " +
            "      and i.available = :available")
    List<ItemEntity> findByTextAndAvailable(
            @Param("text") String text,
            @Param("available") boolean available);

    boolean existsByOwnerId(long userId);

    @Query("select max(b.end) " +
            "from ItemEntity i " +
            "   join BookingEntity b " +
            "where i.id = :itemId " +
            "   and b.end < :date")
    Optional<Timestamp> getLastBooking(
            @Param("itemId") long itemId,
            @Param("date") Timestamp date);

    @Query("select min(b.start) " +
            "from ItemEntity i " +
            "   join BookingEntity b " +
            "where i.id = :itemId " +
            "   and b.start > :date")
    Optional<Timestamp> getNextBooking(
            @Param("itemId") long itemId,
            @Param("date") Timestamp date);

    @Query("select i, max(bLast.end), min(bNext.start) " +
            "from ItemEntity i " +
            "   left join BookingEntity bLast " +
            "   left join BookingEntity bNext " +
            "where i.owner = :owner " +
            "    and bLast.end < :date" +
            "    and bNext.start > :date " +
            "group by i")
    List<ItemWithDateOfBookings> findItemsByOwnerWithDatesOfBooking(
            @Param("owner") long ownerId,
            @Param("date") Timestamp date);

    List<ItemEntity> findByOwnerId(long userId);
}
