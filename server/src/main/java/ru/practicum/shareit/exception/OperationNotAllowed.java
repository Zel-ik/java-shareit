package ru.practicum.shareit.exception;

public class OperationNotAllowed extends RuntimeException {
    public OperationNotAllowed(String message) {
        super(message);
    }
}
