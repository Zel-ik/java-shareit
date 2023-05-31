package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Long, Item> items = new HashMap<>();
    private long generator = 1;

    public void makeGeneratorToOne() {
        generator = 1;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(generator);
        generator++;
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item, long itemId) {
        item.setId(itemId);
        if (item.getName() == null) {
            item.setName(getItemById(itemId).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(getItemById(itemId).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(getItemById(itemId).getAvailable());
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getAllItem(long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItem(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())) ||
                        (item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }


    public void deleteItem(long id) {
        items.remove(id);
    }
}
