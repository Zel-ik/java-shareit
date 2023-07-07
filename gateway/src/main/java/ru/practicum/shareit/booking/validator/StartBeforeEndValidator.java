package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookItemRequestDto> {
    @Override
    public boolean isValid(BookItemRequestDto booking, ConstraintValidatorContext cxt) {
        return booking.getStart().isBefore(booking.getEnd());
    }
}