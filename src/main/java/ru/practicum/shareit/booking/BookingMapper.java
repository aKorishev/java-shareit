package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingEntity;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserEntity;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BookingMapper {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public BookingDto toDto(BookingEntity entity, UserDto userDto, ItemDto itemDto) {
        return BookingDto
                .builder()
                .id(entity.getId())
                .itemId(entity.getItem().getId())
                .status(entity.getStatus())
                .start(format.format(entity.getStart()))
                .end(format.format(entity.getEnd()))
                .booker(userDto)
                .item(itemDto)
                .build();
    }

    public BookingEntity toEntity(BookingDto dto, UserEntity user, ItemEntity item) throws ParseException {
        var entity = new BookingEntity();
        entity.setId(dto.id());
        entity.setBooker(user);
        entity.setItem(item);
        entity.setStatus(dto.status());
        entity.setStart(Timestamp.from(format.parse(dto.start()).toInstant()));
        entity.setEnd(Timestamp.from(format.parse(dto.end()).toInstant()));

        return entity;
    }
}
