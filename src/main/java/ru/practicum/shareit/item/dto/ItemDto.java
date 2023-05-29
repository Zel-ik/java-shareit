package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private  Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 100)
    private String description;
    @NotNull
    private Boolean available; // доступность предмета
    private ItemRequest request; // запрос о создании
}
