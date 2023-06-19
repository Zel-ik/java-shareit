package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    long id;
    @NotBlank
    String name;
    @NotBlank
    @Size(max = 75)
    String description;
    Boolean available; // доступность предмета
    User owner; // Хозяин предмета
    ItemRequest request; // запрос о создании

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
