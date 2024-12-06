package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingEntity;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.storage.ItemEntity;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class BookingServiceTest {
    @Mock
    BookingStorage bookingStorage;
    @Mock
    ItemStorage itemStorage;
    @Mock
    UserStorage userStorage;

    @InjectMocks
    BookingService bookingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void findBookingWhileNotFoundUser() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            bookingService.findBooking(10L, 10L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void findBookingWhileNotFoundBookingId() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));

        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            bookingService.findBooking(10L, 10L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Бронь не найдена", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Бронь не найдена\")");
    }

    @Test
    public void findBookingWhileUserIsNotCreatorAndOwner() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(1L);
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(ownerEntity));

        var itemEntity = new ItemEntity();
        itemEntity.setId(2L);
        itemEntity.setOwner(ownerEntity);

        var bookerEntity = new UserEntity();
        bookerEntity.setId(4L);

        var bookingEntity = new BookingEntity();
        bookingEntity.setId(3L);
        bookingEntity.setItem(itemEntity);
        bookingEntity.setBooker(bookerEntity);

        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        try {
            bookingService.findBooking(10L, 10L);
        } catch (NotValidException ex) {
            Assertions.assertEquals("Данные о бронировании может получить заказчик или владелец", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotValidException(\"Данные о бронировании может получить заказчик или владелец\")");
    }

    @Test
    public void findBookingWhileUserIsNotCreator() {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        var itemEntity = new ItemEntity();
        itemEntity.setId(2L);
        itemEntity.setOwner(userEntity);

        var bookerEntity = new UserEntity();
        bookerEntity.setId(4L);

        var bookingEntity = new BookingEntity();
        bookingEntity.setId(3L);
        bookingEntity.setItem(itemEntity);
        bookingEntity.setBooker(bookerEntity);
        bookingEntity.setStart(Timestamp.from(Instant.now()));
        bookingEntity.setEnd(Timestamp.from(Instant.now()));

        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        bookingService.findBooking(10L, 1L);
    }

    @Test
    public void findBookingWhileUserIsNotItemOwner() {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        var itemEntity = new ItemEntity();
        itemEntity.setId(2L);
        itemEntity.setOwner(userEntity);

        var bookerEntity = new UserEntity();
        bookerEntity.setId(4L);

        var bookingEntity = new BookingEntity();
        bookingEntity.setId(3L);
        bookingEntity.setItem(itemEntity);
        bookingEntity.setBooker(bookerEntity);
        bookingEntity.setStart(Timestamp.from(Instant.now()));
        bookingEntity.setEnd(Timestamp.from(Instant.now()));

        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        bookingService.findBooking(10L, 4L);
    }

    @Test
    public void findBooking() {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        var itemEntity = new ItemEntity();
        itemEntity.setId(2L);
        itemEntity.setOwner(userEntity);

        var bookerEntity = new UserEntity();
        bookerEntity.setId(4L);

        var bookingEntity = new BookingEntity();
        bookingEntity.setId(3L);
        bookingEntity.setItem(itemEntity);
        bookingEntity.setBooker(bookerEntity);
        bookingEntity.setStart(Timestamp.from(Instant.now()));
        bookingEntity.setEnd(Timestamp.from(Instant.now()));

        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        var expectedBookingDto = bookingEntity.toDto();
        var actualBookingDto = bookingService.findBooking(13L, 1L);

        Assertions.assertEquals(expectedBookingDto, actualBookingDto);

        Mockito.verify(bookingStorage, Mockito.times(1))
                .findBooking(13L);
    }

    @Test
    public void findBookingsForUserIdWhileNotFoundUserId() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            bookingService.findBookingsForUserId(10L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void findBookingsForUserId() {
        var userEntity = new UserEntity(); userEntity.setId(2L);

        var itemEntity = new ItemEntity(); itemEntity.setId(3L);

        var expectedBookingEntity1 = new BookingEntity();
        expectedBookingEntity1.setId(4L);
        expectedBookingEntity1.setBooker(userEntity);
        expectedBookingEntity1.setItem(itemEntity);
        expectedBookingEntity1.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity1.setEnd(Timestamp.from(Instant.now()));

        var expectedBookingEntity2 = new BookingEntity();
        expectedBookingEntity2.setId(5L);
        expectedBookingEntity2.setBooker(userEntity);
        expectedBookingEntity2.setItem(itemEntity);
        expectedBookingEntity2.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity2.setEnd(Timestamp.from(Instant.now()));

        var expectedBookingEntity3 = new BookingEntity();
        expectedBookingEntity3.setId(6L);
        expectedBookingEntity3.setBooker(userEntity);
        expectedBookingEntity3.setItem(itemEntity);
        expectedBookingEntity3.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity3.setEnd(Timestamp.from(Instant.now()));

        var bookingEntities = List.of(
                expectedBookingEntity1,
                expectedBookingEntity2,
                expectedBookingEntity3
        );

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        Mockito.when(bookingStorage.findBookingsByBookerId(Mockito.anyLong()))
                .thenReturn(bookingEntities);

        var actualBookingList = bookingService.findBookingsForUserId(4L);
        var expectedBookingList = bookingEntities.stream().map(BookingEntity::toDto).toList();

        Assertions.assertEquals(actualBookingList, expectedBookingList);
    }

    @Test
    public void findBookingsForOwnerIdWhileNotFoundUserId() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            bookingService.findBookingsForItemOwnerId(10L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void findBookingsForOwnerId() {
        var userEntity = new UserEntity(); userEntity.setId(2L);

        var itemEntity = new ItemEntity(); itemEntity.setId(3L);

        var expectedBookingEntity1 = new BookingEntity();
        expectedBookingEntity1.setId(4L);
        expectedBookingEntity1.setBooker(userEntity);
        expectedBookingEntity1.setItem(itemEntity);
        expectedBookingEntity1.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity1.setEnd(Timestamp.from(Instant.now()));

        var expectedBookingEntity2 = new BookingEntity();
        expectedBookingEntity2.setId(5L);
        expectedBookingEntity2.setBooker(userEntity);
        expectedBookingEntity2.setItem(itemEntity);
        expectedBookingEntity2.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity2.setEnd(Timestamp.from(Instant.now()));

        var expectedBookingEntity3 = new BookingEntity();
        expectedBookingEntity3.setId(6L);
        expectedBookingEntity3.setBooker(userEntity);
        expectedBookingEntity3.setItem(itemEntity);
        expectedBookingEntity3.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity3.setEnd(Timestamp.from(Instant.now()));

        var bookingEntities = List.of(
                expectedBookingEntity1,
                expectedBookingEntity2,
                expectedBookingEntity3
        );

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        Mockito.when(bookingStorage.findBookingsByOwnerId(Mockito.anyLong()))
                .thenReturn(bookingEntities);

        var actualBookingList = bookingService.findBookingsForItemOwnerId(6L);
        var expectedBookingList = bookingEntities.stream().map(BookingEntity::toDto).toList();

        Assertions.assertEquals(expectedBookingList, actualBookingList);

        Mockito.verify(bookingStorage, Mockito.times(1))
                .findBookingsByOwnerId(6L);
    }

    @Test
    public void createBookingWhileNotFoundUserId() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            bookingService.createBooking(BookingDto.builder().build(), 1L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void createBookingWhileNotFoundItemId() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));
        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        var dto = BookingDto
                .builder()
                .itemId(10L)
                .build();

        try {
            bookingService.createBooking(dto, 1L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Вещь не найдена", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Вещь не найдена\")");
    }

    @Test
    public void createBookingWhileItemIsNotFree() {
        var itemEntity = new ItemEntity();
        itemEntity.setAvailable(false);

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));
        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var dto = BookingDto
                .builder()
                .itemId(10L)
                .build();

        try {
            bookingService.createBooking(dto, 1L);
        } catch (NotValidException ex) {
            Assertions.assertEquals("Вещь не доступна к бронированию", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotValidException(\"Вещь не доступна к бронированию\")");
    }

    @Test
    public void createBooking() {
        var itemEntity = new ItemEntity();
        itemEntity.setAvailable(true);
        itemEntity.setId(3L);

        var userEntity = new UserEntity();
        userEntity.setId(4L);

        var bookingDto = BookingDto
                .builder()
                .id(5L)
                .itemId(10L)
                .start("2024-12-03T00:00:00")
                .end("2024-12-04T00:00:00")
                .build();

        var expectedBookingEntity = new BookingEntity();
        expectedBookingEntity.setId(5L);
        expectedBookingEntity.setBooker(userEntity);
        expectedBookingEntity.setItem(itemEntity);
        expectedBookingEntity.setStart(Timestamp.valueOf(LocalDateTime.of(2024,12,03,0,0)));
        expectedBookingEntity.setEnd(Timestamp.valueOf(LocalDateTime.of(2024,12,04,0,0)));
        expectedBookingEntity.setStatus(BookingStatus.WAITING);

        var expectedBookingDto = expectedBookingEntity.toDto();

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));
        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var actualBooking = bookingService.createBooking(bookingDto, 7L);

        Assertions.assertEquals(expectedBookingDto, actualBooking);

        Mockito.verify(bookingStorage, Mockito.times(1))
                .updateBooking(expectedBookingEntity);
    }

    @Test
    public void setBookingStatusWhileNotFoundUserId() {
        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(false);

        try {
            bookingService.setBookingStatus(1L, true, 1L);
        } catch (NotValidException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotValidException(\"Пользователь не найден\")");
    }

    @Test
    public void setBookingStatusWhileNotFoundBookingId() {
        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            bookingService.setBookingStatus(1L, true, 1L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Бронь не найдена", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Бронь не найдена\"\")");
    }

    @Test
    public void setBookingStatusWhileUserIdIsNotOwner() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(4L);

        var bookerEntity = new UserEntity();
        bookerEntity.setId(7L);

        var itemEntity = new ItemEntity();
        itemEntity.setAvailable(true);
        itemEntity.setId(3L);
        itemEntity.setOwner(ownerEntity);

        var bookingEntity = new BookingEntity();
        bookingEntity.setId(5L);
        bookingEntity.setBooker(bookerEntity);
        bookingEntity.setItem(itemEntity);
        bookingEntity.setStart(Timestamp.valueOf(LocalDateTime.of(2024,12,03,0,0)));
        bookingEntity.setEnd(Timestamp.valueOf(LocalDateTime.of(2024,12,04,0,0)));
        bookingEntity.setStatus(BookingStatus.WAITING);

        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        try {
            bookingService.setBookingStatus(1L, true, 1L);
        } catch (NotValidException ex) {
            Assertions.assertEquals("Подтвердить бронь может только владелец", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotValidException(\"Подтвердить бронь может только владелец\")");
    }

    @Test
    public void setBookingStatusApprovedTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(4L);

        var bookerEntity = new UserEntity();
        bookerEntity.setId(7L);

        var itemEntity = new ItemEntity();
        itemEntity.setAvailable(true);
        itemEntity.setId(3L);
        itemEntity.setOwner(ownerEntity);

        var bookingEntity = new BookingEntity();
        bookingEntity.setId(5L);
        bookingEntity.setBooker(bookerEntity);
        bookingEntity.setItem(itemEntity);
        bookingEntity.setStart(Timestamp.valueOf(LocalDateTime.of(2024,12,03,0,0)));
        bookingEntity.setEnd(Timestamp.valueOf(LocalDateTime.of(2024,12,04,0,0)));
        bookingEntity.setStatus(BookingStatus.WAITING);

        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        var expectedBookingDto = bookingEntity.toDto()
                .toBuilder()
                .status(BookingStatus.APPROVED)
                .build();

        var actualBooking = bookingService.setBookingStatus(1L, true, 4L);

        Assertions.assertEquals(expectedBookingDto, actualBooking);

        Mockito.verify(bookingStorage, Mockito.times(1))
                .updateBooking(bookingEntity);
    }

    @Test
    public void setBookingStatusRejectTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(4L);

        var bookerEntity = new UserEntity();
        bookerEntity.setId(7L);

        var itemEntity = new ItemEntity();
        itemEntity.setAvailable(true);
        itemEntity.setId(3L);
        itemEntity.setOwner(ownerEntity);

        var bookingEntity = new BookingEntity();
        bookingEntity.setId(5L);
        bookingEntity.setBooker(bookerEntity);
        bookingEntity.setItem(itemEntity);
        bookingEntity.setStart(Timestamp.valueOf(LocalDateTime.of(2024,12,03,0,0)));
        bookingEntity.setEnd(Timestamp.valueOf(LocalDateTime.of(2024,12,04,0,0)));
        bookingEntity.setStatus(BookingStatus.WAITING);

        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingStorage.findBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(bookingEntity));

        var expectedBookingDto = bookingEntity.toDto()
                .toBuilder()
                .status(BookingStatus.REJECTED)
                .build();

        var actualBooking = bookingService.setBookingStatus(1L, false, 4L);

        Assertions.assertEquals(expectedBookingDto, actualBooking);

        Mockito.verify(bookingStorage, Mockito.times(1))
                .updateBooking(bookingEntity);
    }
}
