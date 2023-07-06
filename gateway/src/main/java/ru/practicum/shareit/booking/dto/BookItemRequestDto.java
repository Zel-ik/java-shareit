package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validateInterfaces.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(groups = {Create.class}, message = "не должно равняться null")
    @FutureOrPresent(groups = {Create.class}, message = "должно содержать сегодняшнее число или дату, которая еще не наступила")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime start;
    @NotNull(groups = {Create.class}, message = "не должно равняться null")
    @Future(groups = {Create.class}, message = "должно содержать сегодняшнее число или дату, которая еще не наступила")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime end;
    @NotNull(groups = {Create.class})
    private long itemId;
}

