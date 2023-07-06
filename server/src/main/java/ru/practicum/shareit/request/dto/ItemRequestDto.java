package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank
    @Size(max = 250)
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items = new ArrayList<>();
}
