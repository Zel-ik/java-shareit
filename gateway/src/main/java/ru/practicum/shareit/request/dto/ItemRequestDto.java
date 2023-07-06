package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validateInterfaces.Create;
import ru.practicum.shareit.validateInterfaces.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    @NotBlank(groups = {Create.class, Update.class}, message = "не должно быть пустым")
    @Size(groups = {Create.class, Update.class}, min = 1, max = 255, message = "размер должен находиться в диапазоне" +
            " от 1 до 255")
    private String description;
}
