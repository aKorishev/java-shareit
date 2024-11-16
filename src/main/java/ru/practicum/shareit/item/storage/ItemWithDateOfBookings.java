package ru.practicum.shareit.item.storage;

import java.sql.Timestamp;

public interface ItemWithDateOfBookings {
    ItemEntity getItemEntity();

    Timestamp getLastDateBooking();

    Timestamp getNextDateBooking();
}
