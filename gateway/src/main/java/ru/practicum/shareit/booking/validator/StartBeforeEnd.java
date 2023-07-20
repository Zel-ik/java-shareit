package ru.practicum.shareit.booking.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StartBeforeEndValidator.class)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartBeforeEnd {
    String message() default "Start date is must be before end date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
