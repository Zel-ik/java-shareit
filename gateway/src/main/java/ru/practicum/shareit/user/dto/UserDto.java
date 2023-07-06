package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validateInterfaces.Create;
import ru.practicum.shareit.validateInterfaces.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class, Update.class}, message = "должно иметь формат адреса электронной почты")
    @Size(groups = {Create.class, Update.class}, max = 512)
    private String email;
    @Size(groups = {Create.class, Update.class}, max = 255)
    @NotBlank(groups = {Create.class}, message = "не должно быть пустым")
    private String name;
}
