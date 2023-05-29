package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public final class ItemMapper {
    public static Item toItem(ItemDto dto, User owner){
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .request(dto.getRequest())
                .build();
    }

    public static ItemDto toDto(Item item){
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }
}
