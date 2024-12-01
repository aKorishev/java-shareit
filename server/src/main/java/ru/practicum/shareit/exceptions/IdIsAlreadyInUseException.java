package ru.practicum.shareit.exceptions;

public class IdIsAlreadyInUseException extends RuntimeException {
    public IdIsAlreadyInUseException(String message) {
        super(message);
    }
}
