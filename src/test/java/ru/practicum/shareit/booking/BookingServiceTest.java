package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusRequestDto;
import ru.practicum.shareit.booking.storage.BookingEntity;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemEntity;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    public void getBookingTest() throws Exception {
        var expectedBookingEntity = new BookingEntity();
        expectedBookingEntity.setId(8L);
        expectedBookingEntity.setItem(new ItemEntity());
        expectedBookingEntity.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity.setEnd(Timestamp.from(Instant.now()));

        Mockito.when(userStorage.getUser(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));
        Mockito.when(bookingStorage.getBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedBookingEntity));
        Mockito.when(bookingStorage.userIdIsBookerOrOwner(Mockito.any(BookingEntity.class), Mockito.anyLong()))
                .thenReturn(true);


        try (MockedStatic<BookingMapper> mapper = Mockito.mockStatic(BookingMapper.class)) {
            mapper
                    .when(() -> BookingMapper.toDto(Mockito.any(BookingEntity.class),
                            Mockito.any(UserDto.class),
                            Mockito.any(ItemDto.class)))
                    .thenAnswer(a -> {
                        var entity = a.getArgument(0, BookingEntity.class);
                        return BookingDto.builder()
                                .id(entity.getId())
                                .build();
                    });

            var bookingDto = bookingService.getBooking(1, 1);

            Assertions.assertEquals(8L, bookingDto.id());

            mapper.verify(
                    () -> BookingMapper.toDto(
                            Mockito.any(BookingEntity.class),
                            Mockito.any(UserDto.class),
                            Mockito.any(ItemDto.class)),
                    Mockito.times(1));
        }
    }

    @Test
    public void getBookingWhileUserNotIsOwnerTest() throws Exception {
        var expectedBookingEntity = new BookingEntity();
        expectedBookingEntity.setId(10L);
        expectedBookingEntity.setItem(new ItemEntity());
        expectedBookingEntity.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity.setEnd(Timestamp.from(Instant.now()));

        Mockito.when(userStorage.getUser(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));
        Mockito.when(bookingStorage.getBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedBookingEntity));
        Mockito.when(bookingStorage.userIdIsBookerOrOwner(Mockito.any(BookingEntity.class), Mockito.anyLong()))
                .thenReturn(false);


        try {
            bookingService.getBooking(1,1);

            Assertions.fail();
        } catch (NotValidException ex) {
            Assertions.assertEquals("Данные о бронировании может получить заказчик или владелец", ex.getMessage());
        }
    }

    @Test
    public void getBookingWhileNotFoundUserTest() throws Exception {
        var expectedBookingEntity = new BookingEntity();
        expectedBookingEntity.setId(10L);
        expectedBookingEntity.setItem(new ItemEntity());
        expectedBookingEntity.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity.setEnd(Timestamp.from(Instant.now()));

        Mockito.when(userStorage.getUser(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(bookingStorage.getBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedBookingEntity));
        Mockito.when(bookingStorage.userIdIsBookerOrOwner(Mockito.any(BookingEntity.class), Mockito.anyLong()))
                .thenReturn(true);


        try {
            bookingService.getBooking(1,1);

            Assertions.fail();
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
        }
    }

    @Test
    public void getBookingWhileNotBookingTest() throws Exception {
        Mockito.when(userStorage.getUser(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));
        Mockito.when(bookingStorage.getBooking(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(bookingStorage.userIdIsBookerOrOwner(Mockito.any(BookingEntity.class), Mockito.anyLong()))
                .thenReturn(true);


        try {
            bookingService.getBooking(1,1);

            Assertions.fail();
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Бронь не найдена", ex.getMessage());
        }
    }

    @Test
    public void getBookingForUserIdTest() {
        var expectedBookingDtoList = List.of(
                BookingDto.builder().id(8L).build(),
                BookingDto.builder().id(107L).build());

        var entity1 = new BookingEntity();
        entity1.setId(8L);
        entity1.setItem(new ItemEntity());

        var entity2 = new BookingEntity();
        entity2.setId(107L);
        entity2.setItem(new ItemEntity());

        Mockito.when(userStorage.getUser(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));

        Mockito.when(bookingStorage.findBookingsByBookerId(Mockito.anyLong()))
                .thenReturn(List.of(entity1, entity2));

        try (MockedStatic<BookingMapper> mapper = Mockito.mockStatic(BookingMapper.class)) {
            mapper
                    .when(() -> BookingMapper.toDto(Mockito.any(BookingEntity.class),
                            Mockito.any(UserDto.class),
                            Mockito.any(ItemDto.class)))
                    .thenAnswer(a -> {
                        var entity = a.getArgument(0, BookingEntity.class);
                        return BookingDto.builder()
                                .id(entity.getId())
                                .build();
                    });

            var bookingDtoList = bookingService.getBookingForUserId(1);

            Assertions.assertEquals(2, bookingDtoList.size());
            Assertions.assertEquals(8L, bookingDtoList.getFirst().id(), "Id dto[0] is expected 8L");
            Assertions.assertEquals(107L, bookingDtoList.get(1).id(), "Id dto[0] is expected 107L");

            mapper.verify(
                    () -> BookingMapper.toDto(
                            Mockito.any(BookingEntity.class),
                            Mockito.any(UserDto.class),
                            Mockito.any(ItemDto.class)),
                    Mockito.times(2));
        }
    }

    @Test
    public void getBookingForUserIdWhileNotFoundUserTest() throws Exception {
        var expectedBookingEntity = new BookingEntity();
        expectedBookingEntity.setId(10L);
        expectedBookingEntity.setItem(new ItemEntity());
        expectedBookingEntity.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity.setEnd(Timestamp.from(Instant.now()));

        Mockito.when(userStorage.getUser(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(bookingStorage.getBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedBookingEntity));


        try {
            bookingService.getBookingForUserId(1);

            Assertions.fail();
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
        }
    }

    @Test
    public void getBookingForUserIdWhileChangeStatusAndOwnerIdTest() throws Exception {
        var expectedBookingEntity = new BookingEntity();
        expectedBookingEntity.setId(10L);
        expectedBookingEntity.setItem(new ItemEntity());
        expectedBookingEntity.setStart(Timestamp.from(Instant.now()));
        expectedBookingEntity.setEnd(Timestamp.from(Instant.now()));

        Mockito.when(userStorage.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingStorage.getBooking(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedBookingEntity));

        var statuses = List.of(
                BookingStatusRequestDto.ALL,
                BookingStatusRequestDto.CURRENT,
                BookingStatusRequestDto.PAST,
                BookingStatusRequestDto.FUTURE,
                BookingStatusRequestDto.WAITING,
                BookingStatusRequestDto.REJECTED,
                BookingStatusRequestDto.APPROVED);

        var owners = List.of(1, 7, 19);

        for (var ownerId: owners) {
            for (var requestStatus: statuses) {
                bookingService.getBookingsForItemOwnerId(requestStatus, ownerId);
            }

            Mockito.verify(bookingStorage, Mockito.times(4))
                    .findBookingsByOwnerId(ownerId);

            Mockito.verify(bookingStorage, Mockito.times(1))
                    .findBookingsByOwnerId(ownerId, BookingStatus.WAITING);

            Mockito.verify(bookingStorage, Mockito.times(1))
                    .findBookingsByOwnerId(ownerId, BookingStatus.REJECTED);

            Mockito.verify(bookingStorage, Mockito.times(1))
                    .findBookingsByOwnerId(ownerId, BookingStatus.APPROVED);
        }
    }
}
