package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.shareit.booking.storage.BookingEntity;
import ru.practicum.shareit.booking.storage.BookingStorageInDb;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.storage.ItemEntity;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;
import java.time.Instant;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
       // "driverClassName=org.h2.Driver"
    },
showSql = false)
@ComponentScan(basePackages = "ru.practicum.shareit")
public class BookingStorageInDbTest {
    @Autowired
    BookingStorageInDb bookingStorageInDb;
    @Autowired
    UserStorage userStorage;
    @Autowired
    ItemStorage itemStorage;

    @Test
    public void findBookingAndNotResult() {
        var userEntity = new UserEntity();
        userEntity.setName("test");
        userEntity.setEmail("aa@BB.cc");
        userStorage.updateUser(userEntity);

        var itemEntity = new ItemEntity();
        itemEntity.setName("test");
        itemEntity.setOwner(userEntity);

        itemStorage.updateItem(itemEntity);

        var maxId = 0L;

        for (int i = 0; i < 10; i++) {
            var entity = new BookingEntity();
            entity.setStatus(BookingStatus.APPROVED);
            entity.setBooker(userEntity);
            entity.setItem(itemEntity);
            entity.setStart(Timestamp.from(Instant.now()));
            entity.setEnd(Timestamp.from(Instant.now()));

            bookingStorageInDb.updateBooking(entity);

            var id = entity.getId();
            if (maxId < id) maxId = id;
        }

        var actualBookingEntity = bookingStorageInDb.findBooking(maxId + 1L);
        Assertions.assertTrue(actualBookingEntity.isEmpty());
    }

    @Test
    public void findBooking() {
        var userEntity = new UserEntity();
        userEntity.setName("test");
        userEntity.setEmail("aa@BB.cc");
        userStorage.updateUser(userEntity);

        var itemEntity = new ItemEntity();
        itemEntity.setName("test");
        itemEntity.setOwner(userEntity);

        itemStorage.updateItem(itemEntity);

        var entity = new BookingEntity();
        entity.setStatus(BookingStatus.APPROVED);
        entity.setBooker(userEntity);
        entity.setItem(itemEntity);
        entity.setStart(Timestamp.from(Instant.now()));
        entity.setEnd(Timestamp.from(Instant.now()));

        bookingStorageInDb.updateBooking(entity);

        var actualBookingEntity = bookingStorageInDb.findBooking(entity.getId());
        Assertions.assertTrue(actualBookingEntity.isPresent());
        Assertions.assertEquals(entity, actualBookingEntity.get());
    }

    @Test
    public void updateBooking() {
        var userEntity = new UserEntity();
        userEntity.setName("test");
        userEntity.setEmail("aa@BB.cc");
        userStorage.updateUser(userEntity);

        var itemEntity = new ItemEntity();
        itemEntity.setName("test");
        itemEntity.setOwner(userEntity);

        itemStorage.updateItem(itemEntity);

        var entity = new BookingEntity();
        entity.setStatus(BookingStatus.APPROVED);
        entity.setBooker(userEntity);
        entity.setItem(itemEntity);

        entity.setStart(Timestamp.from(Instant.now()));
        entity.setEnd(Timestamp.from(Instant.now()));

        var otherUserEntity = new UserEntity();
        otherUserEntity.setName("test2");
        otherUserEntity.setEmail("aa2@BB.cc");
        userStorage.updateUser(otherUserEntity);

        bookingStorageInDb.updateBooking(entity); //Создание записи

        entity.setBooker(otherUserEntity);

        bookingStorageInDb.updateBooking(entity);

        Assertions.assertEquals(otherUserEntity, entity.getBooker());
    }
}
