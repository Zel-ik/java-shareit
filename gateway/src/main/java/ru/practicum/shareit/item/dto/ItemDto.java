package ru.practicum.shareit.item.dto;

import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validateInterfaces.Create;
import ru.practicum.shareit.validateInterfaces.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @NotBlank(groups = Create.class)
    @Size(groups = {Create.class, Update.class}, max = 100)
    private String name;
    @NotBlank(groups = Create.class)
    @Size(groups = {Create.class, Update.class}, max = 500)
    private String description;
    @BooleanFlag
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long requestId;
}
