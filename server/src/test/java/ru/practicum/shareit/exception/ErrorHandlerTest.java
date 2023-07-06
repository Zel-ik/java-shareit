package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTest {
    @Mock
    private ConflictException conflictException;
    @Mock
    private EntityNotFoundException entityNotFoundException;
    @Mock
    private BadRequestException badRequestException;
    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    public void testConflict() {
        String message = "conflict";
        when(conflictException.getMessage()).thenReturn(message);

        ErrorHandler.ErrorResponse response = errorHandler.conflict(conflictException);

        assertEquals(message, response.getError());
    }

    @Test
    public void testNotFound() {
        String message = "not found";
        when(entityNotFoundException.getMessage()).thenReturn(message);

        ErrorHandler.ErrorResponse response = errorHandler.notFound(entityNotFoundException);

        assertEquals(message, response.getError());
    }

    @Test
    public void testBadRequest() {
        String message = "bad request";
        when(badRequestException.getMessage()).thenReturn(message);

        ErrorHandler.ErrorResponse response = errorHandler.badRequest(badRequestException);

        assertEquals(message, response.getError());
    }

    @Test
    public void testConstructor() {
        String message = "This is a conflict exception.";
        ConflictException exception = new ConflictException(message);
        assertEquals(message, exception.getMessage());
    }
}