package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Builder(toBuilder = true)
public record BookingDto(
        Long id,
        Long itemId,
        BookingStatus status,
        String start,
        String end,
        UserDto booker,
        ItemDto item
){ }
