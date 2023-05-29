package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Service
public interface ItemService {
     Item addItem(Item item);

     Item getItemById(long itemId);

     Item updateItem(Item item, long itemId);

     Collection<Item> getAllItem(long userId);

     Collection<Item> searchItem(String text);
}
