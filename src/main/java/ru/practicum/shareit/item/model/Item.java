package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    private  Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 75)
    private String description;
    @NotNull
    private Boolean available; // доступность предмета
    private User owner; // Хозяин предмета
    private ItemRequest request; // запрос о создании

}
