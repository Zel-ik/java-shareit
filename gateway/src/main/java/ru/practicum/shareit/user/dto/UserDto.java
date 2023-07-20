package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validationgroup.CreateGroup;
import ru.practicum.shareit.validationgroup.UpdateGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @NotBlank(groups = CreateGroup.class)
    @Size(max = 50)
    String name;
    @Email(groups = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(groups = CreateGroup.class)
    @Size(max = 50)
    String email;
}
