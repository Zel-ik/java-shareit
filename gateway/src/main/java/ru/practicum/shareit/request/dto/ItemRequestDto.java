package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ItemRequestDto {
    @NotBlank
    @Size(max = 250)
    private String description;
}
