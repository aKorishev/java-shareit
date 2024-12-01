package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ReviewDto {
    Long id;
    Long bookingId;
    String text;
    boolean isSuccess;
}
