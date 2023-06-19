package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepositoryImpl;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl repository;

    @Override
    public Item addItem(Item item) {
        return repository.addItem(item);
    }

    @Override
    public Item getItemById(long itemId) {
        return repository.getItemById(itemId);
    }

    @Override
    public Item updateItem(Item item, long itemId) {
        checkItem(itemId);
        return repository.updateItem(item, itemId);
    }

    @Override
    public Collection<Item> getAllItem(long userId) {
        return repository.getAllItem(userId);
    }

    @Override
    public Collection<Item> searchItem(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return repository.searchItem(text);
    }

    void checkItem(long itemId) {
        Item item = repository.getItemById(itemId);
        if (repository.getItemById(itemId) == null) {
            throw new NotFoundException("Item with id " + itemId + " not found.");
        }
        if (item.getOwner() == null) {
            throw new NotFoundException("Item with id " + itemId + " has no User(owner)");
        }
    }
}
