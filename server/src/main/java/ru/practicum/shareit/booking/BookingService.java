package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusRequestDto;
import ru.practicum.shareit.booking.storage.BookingEntity;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;

import java.text.ParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;

    public BookingDto getBooking(long bookingId, long userId) {
        var userEntity = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        var bookingEntity = bookingStorage.getBooking(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        if (!bookingStorage.userIdIsBookerOrOwner(bookingEntity, userId)) {
            throw new NotValidException("Данные о бронировании может получить заказчик или владелец");
        }

        return BookingMapper.toDto(
                bookingEntity,
                UserMapper.toDto(userEntity),
                ItemMapper.toDto(bookingEntity.getItem()));
    }

    public List<BookingDto> getBookingForUserId(long userId) {
        return getBookingForUserId(BookingStatusRequestDto.ALL, userId);
    }

    public List<BookingDto> getBookingForUserId(BookingStatusRequestDto state, long userId) {
        var userEntity = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<BookingEntity> bookingEntities = switch (state) {
            case ALL, CURRENT, PAST, FUTURE -> bookingStorage.findBookingsByBookerId(userId);
            case WAITING ->  bookingStorage.findBookingsByBookerId(userId, BookingStatus.WAITING);
            case REJECTED ->  bookingStorage.findBookingsByBookerId(userId, BookingStatus.REJECTED);
            case APPROVED ->  bookingStorage.findBookingsByBookerId(userId, BookingStatus.APPROVED);
            default -> List.of();
        };

        return bookingEntities
                .stream()
                .map(i -> BookingMapper.toDto(
                        i,
                        UserMapper.toDto(userEntity),
                        ItemMapper.toDto(i.getItem())))
                .toList();
    }

    public List<BookingDto> getBookingsForItemOwnerId(long userId) {
        return getBookingsForItemOwnerId(BookingStatusRequestDto.ALL, userId);
    }

    public List<BookingDto> getBookingsForItemOwnerId(BookingStatusRequestDto state, long ownerId) {
        if (!userStorage.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<BookingEntity> bookingEntities = switch (state) {
            case ALL, CURRENT, PAST, FUTURE -> bookingStorage.findBookingsByOwnerId(ownerId);
            case WAITING ->  bookingStorage.findBookingsByOwnerId(ownerId, BookingStatus.WAITING);
            case REJECTED ->  bookingStorage.findBookingsByOwnerId(ownerId, BookingStatus.REJECTED);
            case APPROVED ->  bookingStorage.findBookingsByOwnerId(ownerId, BookingStatus.APPROVED);
            default -> List.of();
        };

        return bookingEntities
                .stream()
                .map(i -> BookingMapper.toDto(
                        i,
                        UserMapper.toDto(i.getBooker()),
                        ItemMapper.toDto(i.getItem())))
                .toList();
    }

    public BookingDto createBooking(BookingDto bookingDto, long userId) {
        var userEntity = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        var itemEntity = itemStorage.getItem(bookingDto.itemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!itemEntity.isAvailable()) {
            throw new NotValidException("Вещь не доступна к бронированию");
        }

        try {
            var bookingEntity = BookingMapper.toEntity(bookingDto, userEntity, itemEntity);
            bookingEntity.setStatus(BookingStatus.WAITING);

            bookingStorage.updateBooking(bookingEntity);

            return BookingMapper.toDto(
                    bookingEntity,
                    UserMapper.toDto(userEntity),
                    ItemMapper.toDto(itemEntity));
        } catch (ParseException e) {
            throw new NotValidException("Произошла ошибка чтения данных");
        }
    }

    public BookingDto setBookingStatus(long id, boolean approved, long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotValidException("Пользователь не найден");
        }

        var bookingEntity = bookingStorage.getBooking(id)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        var itemEntity = bookingEntity.getItem();

        if (itemEntity.getOwner().getId() != userId) {
            throw new NotValidException("Подтвердить бронь может только владелец");
        }

        if (approved)
            bookingEntity.setStatus(BookingStatus.APPROVED);
        else
            bookingEntity.setStatus(BookingStatus.REJECTED);

        bookingStorage.updateBooking(bookingEntity);

        return BookingMapper.toDto(
                bookingEntity,
                UserMapper.toDto(bookingEntity.getBooker()),
                ItemMapper.toDto(itemEntity));
    }
}
