package ru.practicum.shareit.item.storage;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ItemEntity {
    long id;
    long owner;
    String name;
    String description;
    boolean available;
}
