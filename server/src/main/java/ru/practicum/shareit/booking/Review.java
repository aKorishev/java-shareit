package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Review {
    Long id;
    Long bookingId;
    String text;
    boolean isSuccess;
}
