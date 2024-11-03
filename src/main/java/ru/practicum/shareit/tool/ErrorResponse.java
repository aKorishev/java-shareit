package ru.practicum.shareit.tool;

import lombok.Value;

@Value
public class ErrorResponse {
    public String error;
    public String description;
}
