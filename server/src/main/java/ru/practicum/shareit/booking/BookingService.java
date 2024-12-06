package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
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

    public BookingDto findBooking(long bookingId, long userId) {
        var userEntity = userStorage.findUserId(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        var bookingEntity = bookingStorage.findBooking(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        if (bookingEntity.getBooker().getId() != userId &&
                bookingEntity.getItem().getOwner().getId() != userId)
            throw new NotValidException("Данные о бронировании может получить заказчик или владелец");

        return bookingEntity.toDto();
    }

    public List<BookingDto> findBookingsForUserId(long userId) {
        if (userStorage.findUserId(userId).isEmpty())
            throw  new NotFoundException("Пользователь не найден");

        var entities = bookingStorage.findBookingsByBookerId(userId);

        return entities
                .stream()
                .map(BookingEntity::toDto)
                .toList();
    }

    public List<BookingDto> findBookingsForItemOwnerId(long userId) {
        if (userStorage.findUserId(userId).isEmpty())
            throw  new NotFoundException("Пользователь не найден");

        var entities = bookingStorage.findBookingsByOwnerId(userId);

        return entities
                .stream()
                .map(BookingEntity::toDto)
                .toList();
    }

    public BookingDto createBooking(BookingDto bookingDto, long userId) {
        var userEntity = userStorage.findUserId(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        var itemEntity = itemStorage.findItem(bookingDto.itemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!itemEntity.isAvailable()) {
            throw new NotValidException("Вещь не доступна к бронированию");
        }

        try {
            var bookingEntity = BookingMapper.toEntity(bookingDto, userEntity, itemEntity);
            bookingEntity.setStatus(BookingStatus.WAITING);

            bookingStorage.updateBooking(bookingEntity);

            return bookingEntity.toDto();
        } catch (ParseException e) {
            throw new NotValidException("Произошла ошибка чтения данных");
        }
    }

    public BookingDto setBookingStatus(long id, boolean approved, long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotValidException("Пользователь не найден");
        }

        var bookingEntity = bookingStorage.findBooking(id)
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
