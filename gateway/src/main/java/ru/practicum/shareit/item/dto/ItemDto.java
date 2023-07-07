package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validationgroup.CreateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    @NotBlank(groups = CreateGroup.class)
    @Size(max = 50)
    String name;
    @NotBlank(groups = CreateGroup.class)
    @Size(max = 250)
    String description;
    @NotNull(groups = CreateGroup.class)
    Boolean available;
    Long requestId;
}