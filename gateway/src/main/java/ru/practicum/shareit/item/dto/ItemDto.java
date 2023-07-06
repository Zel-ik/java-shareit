package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validationgroup.CreateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ItemDto {
    @NotBlank(groups = CreateGroup.class)
    @Size(max = 50)
    private String name;
    @NotBlank(groups = CreateGroup.class)
    @Size(max = 250)
    private String description;
    @NotNull(groups = CreateGroup.class)
    private Boolean available;
    private Long requestId;
}