package ru.practicum.shareit.user.storage;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class UserEntity {
    long id;
    String name;
    String email;
}
